package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/info/v1/movie",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
})
public class MoviesControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void retrieveMovieById(){


        //given
        var movieId = "100";
        stubFor(get(urlEqualTo("/info/v1/movie/"+movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("movieinfo.json")
                )
        );
        stubFor(get(urlPathEqualTo("v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("reviews.json")
                )
        );
        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(response -> {
                    var movie = response.getResponseBody();
                    assert movie != null;
                    System.out.println(movie);
                });

        //then
    }


    @Test
    void retrieveMovieById_404(){


        //given
        var movieId = "100";
        stubFor(get(urlEqualTo("/info/v1/movie/"+movieId))
                .willReturn(aResponse()
                        .withStatus(404)
                )
        );
        stubFor(get(urlPathEqualTo("v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBodyFile("reviews.json")
                )
        );
        //when
        webTestClient
                .get()
                .uri("/v1/movies/{id}",movieId)
                .exchange()
                .expectStatus().is4xxClientError();

        //then
    }



}
