package com.reactivespring.router;


import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){

        /*return route()
                .GET("/v1/test",(request->ServerResponse.ok().bodyValue("Hello world")))
                .POST("/v1/reviews",request->reviewHandler.addReview(request))
                .GET("/v1/reviews",request -> reviewHandler.getAllReview(request))
                .build();*/


        //Nested or grouping of API's

        return route()
                .nest(path("/v1/reviews"),builder -> {
                    builder.POST("",request -> reviewHandler.addReview(request))
                            .GET("",request ->reviewHandler.getAllReview(request) )
                            .PUT("/{id}",request -> reviewHandler.updateReview(request))
                            .DELETE("/{id}",request -> reviewHandler.deleteReview(request))
                            .GET("/id",request -> reviewHandler.getReviewByMovieId(request))
                            .GET("/stream",request -> reviewHandler.getReviewStream(request));

                })
                .GET("/v1/test",(request->ServerResponse.ok().bodyValue("Hello world")))
                .build();
    }
}
