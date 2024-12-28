package com.reactivespring.controller;

import com.reactivespring.client.MovieInfoRestClient;
import com.reactivespring.client.ReviewRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MovieController {

    private MovieInfoRestClient movieInfoRestClient;

    private ReviewRestClient reviewRestClient;

    public MovieController(MovieInfoRestClient movieInfoRestClient, ReviewRestClient reviewRestClient) {
        this.movieInfoRestClient = movieInfoRestClient;
        this.reviewRestClient = reviewRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable("id") String movieId) {

        return movieInfoRestClient.retriveMovieInfo(movieId)
                .flatMap(movieInfo -> reviewRestClient.getAllReviews(movieId)
                        .collectList()
                        .map(reviews -> new Movie(movieInfo, reviews))
                );
    }

    @GetMapping(value = "/stream",produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> retriveMovieInfos() {

        return movieInfoRestClient.retriveMovieInfoStream();
    }
}
