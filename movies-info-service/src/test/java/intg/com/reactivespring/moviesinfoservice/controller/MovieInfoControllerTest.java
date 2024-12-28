package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repo.MovieInfoRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerTest {

    @Autowired
    MovieInfoRepo movieInfoRepo;


    @Autowired
    WebTestClient webTestClient;

    private String URL = "/info/v1/movie";

    @BeforeEach
    void setUp() {

        List<MovieInfo> movieInfoList = List.of(
                new MovieInfo("1", "Hanuman", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22")),
                new MovieInfo("2", "Bal Ganesh", 2002, List.of("Shiva", "Parvathi"), LocalDate.parse("2002-07-14")),
                new MovieInfo("3", "Travel", 1987, List.of("TEMP", "CORe"), LocalDate.parse("1987-02-07"))
        );

        movieInfoRepo.saveAll(movieInfoList).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepo.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        var input = new MovieInfo(null, "Hanuman1", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .post()
                .uri(URL)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {

                    var movieInfo = response.getResponseBody();

                    assertNotNull(movieInfo.getMovieInfoId());

                });
    }

    @Test
    void getAllMovieInfo() {

        // var input = new MovieInfo(null, "Hanuman1", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .get()
                .uri(URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfo() {

        var movieInfoId="1";
        //var updated = new MovieInfo(null, "Hanuman1", 2000, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .get()
                .uri(URL+"/{id}"   , movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {

                    var movieInfo = response.getResponseBody();

                    assertNotNull(movieInfo.getMovieInfoId());

                });
    }

    @Test
    void updateMovieInfo() {

        var movieInfoId = "1";
        var input = new MovieInfo("1", "Hanuman1", 2000, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .put()
                .uri(URL+"/{id}"   , movieInfoId)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {

                    var movieInfo = response.getResponseBody();

                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals(2000, movieInfo.getYear());

                });
    }

    @Test
    void deleteMovieInfo() {

        var movieInfoId = "3";
        webTestClient
                .delete()
                .uri(URL+"/{id}"   , movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }



    /**
     * To show ResponseEntity with Webflux
     */

    @Test
    void updateMovieInfo_InvalidId() {

        var input = new MovieInfo("1", "Hanuman1", 2000, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));


        webTestClient
                .put()
                .uri(URL+"/{id}"   , "100")
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    /**
     * To show ResponseEntity with Webflux
     */
    @Test
    void getMovieInfo_InvlaidId() {

        var movieInfoId="800";
        //var updated = new MovieInfo(null, "Hanuman1", 2000, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .get()
                .uri(URL+"/{id}"   , movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void getAllMovieInfoByYear() {

       var uriBuilder= UriComponentsBuilder.fromUriString(URL)
                        .queryParam("year",1818)
                        .buildAndExpand()
                        .toUri();

        webTestClient
                .get()
                .uri(uriBuilder)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getAllMovies_stream() {

        var input = new MovieInfo(null, "Hanuman1", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));
        webTestClient
                .post()
                .uri(URL)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {

                    var movieInfo = response.getResponseBody();

                    assertNotNull(movieInfo.getMovieInfoId());

                });

        var moviesStreamFlux=  webTestClient.get()
                .uri(URL+"/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(moviesStreamFlux)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1);
                    assert movieInfo1.getMovieInfoId()!=null;
                })
                .thenCancel()
                .verify();
    }



}