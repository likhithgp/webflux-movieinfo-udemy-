package com.reactivespring.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void flux() {

        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(5);
    }



    @Test
    void flux_Approach2() {

      Flux<Integer> result = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }


    @Test
    void flux_Approach3() {

        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    List<Integer> res = response.getResponseBody();

                    assert (Objects.requireNonNull(res).size()==5);
                });


    }


    @Test
    void mono() {

        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String res = response.getResponseBody();

                    assertEquals ("Hello World",res);
                });


    }


    @Test
    void stream() {

        Flux<Long> result = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(0L,1L,2L,3L)
                .thenCancel()
                .verify();


    }
}