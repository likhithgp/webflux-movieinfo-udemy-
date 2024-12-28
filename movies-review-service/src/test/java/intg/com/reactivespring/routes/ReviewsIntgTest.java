package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.repo.ReviewReactiveRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@AutoConfigureWebClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepo reviewReactiveRepo;

    static String URL = "v1/reviews";

    @BeforeEach
    void setup(){
        var reviewList = List.of(
                new Review(null,1L,"Waste of time",1.0),
                new Review(null,2L,"No need to watch",2.0),
                new Review("1",100L,"Save Money",5.0)
        );

        reviewReactiveRepo.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown(){
           reviewReactiveRepo.deleteAll().block();
    }

     @Test
    void addReview(){

        var review = new Review(null,300L,"Just watch for entertainment",4.0);

         webTestClient
                 .post()
                 .uri(URL)
                 .bodyValue(review)
                 .exchange()
                 .expectStatus()
                 .isCreated()
                 .expectBody(Review.class)
                 .consumeWith(response -> {

                     var savedReview = response.getResponseBody();

                     assert savedReview != null;
                     assertNotNull(savedReview.getReviewId());
                     assertEquals(4.0,savedReview.getRating());


                 });
     }



    @Test
    void getAllReviews() {

        webTestClient
                .get()
                .uri(URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }




    @Test
    void deleteMovieReview() {

        var id = "1";
        webTestClient
                .delete()
                .uri(URL+"/{id}"   , id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }


    @Test
    void updateMovieInfo() {

        var movieInfoId = "1";
        var input = new Review("1",100L,"Save Money will be helpful",4.0);
        webTestClient
                .put()
                .uri(URL+"/{id}"   , movieInfoId)
                .bodyValue(input)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(response -> {

                    var review = response.getResponseBody();

                    assertEquals(100L,review.getMovieInfoId());
                    assertEquals("1", review.getReviewId());
                    assertEquals("Save Money will be helpful", review.getComment());
                    assertEquals(4.0, review.getRating());

                });
    }

}
