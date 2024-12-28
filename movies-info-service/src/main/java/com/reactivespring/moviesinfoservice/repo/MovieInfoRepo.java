package com.reactivespring.moviesinfoservice.repo;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;


public interface MovieInfoRepo extends ReactiveMongoRepository<MovieInfo, String> {

    /**
     * Custom reactive mongo query to return movie info based on yER
     * @return
     */
    Flux<MovieInfo> findByYear(int year);
}
