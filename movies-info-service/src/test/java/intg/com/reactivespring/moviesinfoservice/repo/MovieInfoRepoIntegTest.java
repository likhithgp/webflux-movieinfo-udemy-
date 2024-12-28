package com.reactivespring.moviesinfoservice.repo;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepoIntegTest {

    @Autowired
    MovieInfoRepo movieInfoRepo;

    @BeforeEach
    void setUp() {

        List<MovieInfo> movieInfoList = List.of(
                new MovieInfo(null,"Hanuman", 1818,List.of("Shiva","Ram"), LocalDate.parse("1999-06-22")),
                new MovieInfo("2","Bal Ganesh", 2002,List.of("Shiva","Parvathi"),LocalDate.parse("2002-07-14")),
                new MovieInfo("3","Travel", 1987,List.of("TEMP","CORe"),LocalDate.parse("1987-02-07"))
        );

        movieInfoRepo.saveAll(movieInfoList).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepo.deleteAll().block();
    }

    @Test
    void findAll(){

        Flux<MovieInfo> moviesList = movieInfoRepo.findAll().log();
        StepVerifier.create(moviesList)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById(){

        Mono<MovieInfo> moviesList = movieInfoRepo.findById("2").log();
        StepVerifier.create(moviesList)
                //.expectNextCount(1)
                .assertNext(movieInfo -> {
                    Assertions.assertEquals("Bal Ganesh",movieInfo.getName());
                })
                .verifyComplete();
    }



    @Test
    void save(){

        Mono<MovieInfo> moviesList = movieInfoRepo.save(
                new MovieInfo(null,"Hanuman Strong", 1818,List.of("Shiva","Ram"), LocalDate.parse("1999-06-22"))).log();
        StepVerifier.create(moviesList)
                //.expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    Assertions.assertEquals("Hanuman Strong",movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void update(){

        MovieInfo movies = movieInfoRepo.findById("2").block();
        movies.setYear(40050);
        Mono<MovieInfo> moviesSave = movieInfoRepo.save(movies);
        StepVerifier.create(moviesSave)
                //.expectNextCount(1)
                .assertNext(movieInfo -> {
                    Assertions.assertEquals(40050,movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void delete(){

        movieInfoRepo.deleteById("2").block();
        Flux<MovieInfo> moviesList = movieInfoRepo.findAll().log();

        StepVerifier.create(moviesList)
               .expectNextCount(2)
                .verifyComplete();
    }

    /**
     * Customer query test to find movie info based on year
     */
    @Test
    void findByYear() {

        var movieInfo = movieInfoRepo.findByYear(1818).log();

        StepVerifier.create(movieInfo)
                .expectNextCount(1)
                .verifyComplete();

    }
}