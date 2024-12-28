package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repo.ReviewReactiveRepo;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewHandler.class, ReviewRouter.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepo repo;

    @Autowired
    private WebTestClient webTestClient;

    static String URL = "v1/reviews";

    @Test
    void addReview(){

        var input = new Review(null,  1818L,"Comment",1.0);

        when(repo.save(any())).thenReturn(Mono.just(input));

        webTestClient
                .post()
                .uri(URL)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(response -> {

                    var review = response.getResponseBody();

                    assertNotNull(review.getReviewId());
                    assertEquals("Comment",review.getComment());

                });
    }
}
