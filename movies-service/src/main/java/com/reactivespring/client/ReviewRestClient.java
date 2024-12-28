package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewRestClient {

    private WebClient webClient;

    @Value("${restClient.reviewUrl}")
    private String reviewUrl;

    public ReviewRestClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public Flux<Review> getAllReviews(String movieId) {

        var url = UriComponentsBuilder.fromHttpUrl(reviewUrl + "/id")
                .queryParam("movieInfoId", movieId)
                .buildAndExpand()
                .toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Status code is: {}",clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                return Mono.error(new MoviesInfoClientException(errorBody,
                                        clientResponse.statusCode().value()));
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {

                    log.error("Server error: "+clientResponse.statusCode().value());

                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody ->
                                    Mono.error(new MoviesInfoServerException("Review service Server exception: "+errorBody))
                            );
                })
                .bodyToFlux(Review.class)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }

}
