package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repo.ReviewReactiveRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Validator;


@Component
@Slf4j
public class ReviewHandler {



   // private Validator validator;

    private ReviewReactiveRepo reviewReactiveRepo;

    Sinks.Many<Review> reviewSink = Sinks.many().replay().all();

    public ReviewHandler(ReviewReactiveRepo reviewReactiveRepo) {

        this.reviewReactiveRepo = reviewReactiveRepo;
        //this.validator = validator;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        //code before adding validator for bean validation
       return request.bodyToMono(Review.class)
               .flatMap(review -> reviewReactiveRepo.save(review))
               .doOnNext(reviewSink::tryEmitNext)
               .flatMap(saved-> ServerResponse.status(HttpStatus.CREATED)
                       .bodyValue(saved));

       /* return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(review -> reviewReactiveRepo.save(review))
                .flatMap(saved-> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(saved));*/
    }

   /* private void validate(Review review) {

       var constraintViolation =  validator.validate(review);
       log.info("constraint Violation: {} ",constraintViolation);

       if(constraintViolation.size()>0){

           var errorMessage = constraintViolation.stream()
                   .map(ConstraintViolation::getMessage)
                   .collect(Collectors.joining(","));

           throw new ReviewDataException(errorMessage);
       }
    }*/

    public Mono<ServerResponse> getAllReview(ServerRequest request) {

        var reviewsFlux = reviewReactiveRepo.findAll();

        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");

        var existingData  = reviewReactiveRepo.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found given id "+reviewId)));

       return existingData
               .flatMap(review -> request.bodyToMono(Review.class)
                .map(reqReview->{
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return  review;
                })
                .flatMap(reviewReactiveRepo::save)
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
        );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");

        var existingData  = reviewReactiveRepo.findById(reviewId);

        return existingData
                .flatMap(review -> reviewReactiveRepo.deleteById(reviewId)
                .then(ServerResponse.noContent().build()));

    }

    public Mono<ServerResponse> getReviewByMovieId(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()) {
            var reviewsFlux = reviewReactiveRepo.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return ServerResponse.ok().body(reviewsFlux,Review.class);
        }else{
            var reviewsFlux = reviewReactiveRepo.findAll();
            return ServerResponse.ok().body(reviewsFlux,Review.class);
        }
    }

    public Mono<ServerResponse> getReviewStream(ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(),Review.class)
                .log();
    }
}
