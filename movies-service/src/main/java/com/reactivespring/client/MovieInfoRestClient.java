package com.reactivespring.client;

import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@Slf4j
public class MovieInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    public MovieInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retriveMovieInfo(String movieId) {

        //var url = moviesInfoUrl.concat("/{id}");

       /* var retrySpec = Retry.fixedDelay(3, java.time.Duration.ofSeconds(2))//3 retries, with each retry after 2 seconds
                .filter(ex->ex instanceof MoviesInfoServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                    Exceptions.propagate(retrySignal.failure())
                );*/

        return webClient.
                get()
                .uri(moviesInfoUrl+"/"+movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("client error: "+clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.error(new MoviesInfoClientException("Movie not found for id: "+movieId,
                                clientResponse.statusCode().value()));
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
                                Mono.error(new MoviesInfoServerException("Movie-info service Server exception: "+errorBody))
                            );
                })
                .bodyToMono(MovieInfo.class)
                //.retry(3)//retry API 3 times if it fails.
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }

    public Flux<MovieInfo> retriveMovieInfoStream() {

        var url = moviesInfoUrl.concat("/stream");

        return webClient.
                get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("client error: "+clientResponse.statusCode().value());
                    if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.error(new MoviesInfoClientException("Movie not found for",
                                clientResponse.statusCode().value()));
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
                                    Mono.error(new MoviesInfoServerException("Movie-info service Server exception: "+errorBody))
                            );
                })
                .bodyToFlux(MovieInfo.class)
                //.retry(3)//retry API 3 times if it fails.
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }
}
