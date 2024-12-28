package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MovieInfoService movieInfoService;

    private String URL = "/info/v1/movie";

   @Test
   void getAllMovies() {
       List<MovieInfo> movieInfoList = List.of(
               new MovieInfo("1", "Hanuman", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22")),
               new MovieInfo("2", "Bal Ganesh", 2002, List.of("Shiva", "Parvathi"), LocalDate.parse("2002-07-14")),
               new MovieInfo("3", "Travel", 1987, List.of("TEMP", "CORe"), LocalDate.parse("1987-02-07"))
       );


       when(movieInfoService.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfoList));

       webClient.get()
               .uri(URL)
               .exchange()
               .expectStatus()
               .is2xxSuccessful()
               .expectBodyList(MovieInfo.class)
               .hasSize(3);
   }


   @Test
    void getMovieInfoById(){

       var movieInfoId = "1";

       when(movieInfoService.getMovieInfo(any())).thenReturn(Mono.just( new MovieInfo("1", "Hanuman", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"))));

       webClient
               .get()
               .uri(URL+"/{id}", movieInfoId)
               .exchange()
               .expectStatus()
               .is2xxSuccessful()
               .expectBody(MovieInfo.class)
               .consumeWith(response -> {

                   var movieInfo = response.getResponseBody();

                   assertNotNull(movieInfo.getMovieInfoId());
                   assertEquals(movieInfo.getMovieInfoId(),"1");
                   assertEquals(movieInfo.getYear(),1818);

               });
   }


   @Test
    void testAddMovieInfo(){


       var input = new MovieInfo("1", "Hanuman", 1818, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));

       when(movieInfoService.addMovieInfo(any())).thenReturn(Mono.just(input));

       webClient
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
                   assertEquals("Hanuman",movieInfo.getName());

               });
   }

    @Test
    void updateMovieInfo() {

        var movieInfoId = "1";
        var input = new MovieInfo("1", "Hanuman1", 2000, List.of("Shiva", "Ram"), LocalDate.parse("1999-06-22"));

        when(movieInfoService.updateMovieInfo(any(),any())).thenReturn(Mono.just(input));

        webClient
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
        when(movieInfoService.deleteMovieInfo(any())).thenReturn(Mono.empty());
        webClient
                .delete()
                .uri(URL+"/{id}"   , movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }


    @Test
    void testAddMovieInfo_Validation(){

//Validation falied for negative year value(-1818)
        var input = new MovieInfo("1", "Hanuman", -1818, List.of(""), LocalDate.parse("1999-06-22"));

        when(movieInfoService.addMovieInfo(any())).thenReturn(Mono.just(input));

        webClient
                .post()
                .uri(URL)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {

                    var responseBody = response.getResponseBody();

                    assertNotNull(responseBody);
                    System.out.println(responseBody);

                });
    }




}
