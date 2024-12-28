package com.reactivespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(excludeName  = {"de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration"})
public class MoviesReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesReviewServiceApplication.class, args);
	}

}
