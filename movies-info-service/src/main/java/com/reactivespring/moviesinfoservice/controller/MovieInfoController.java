package com.reactivespring.moviesinfoservice.controller;


import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@RestController
@RequestMapping("/info/v1")
public class MovieInfoController {

    private MovieInfoService movieInfoService;

     Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().all();

   public MovieInfoController(MovieInfoService movieInfoService) {

       this.movieInfoService = movieInfoService;
   }

    @PostMapping("/movie")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovie(@RequestBody @Valid MovieInfo movieInfo) {
       System.out.println(movieInfo);
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(movieInfoSink::tryEmitNext)
                .log();

        //publish add movie info

    }

    @GetMapping(value = "/movie/stream",produces = MediaType.APPLICATION_NDJSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> streamSavedMovie(@RequestParam(value = "year",required = false) Integer year) {

        return movieInfoSink.asFlux().log();
    }

    @GetMapping("/movie")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovie(@RequestParam(value = "year",required = false) Integer year) {

       if(year!=null){
           return movieInfoService.findByYear(year);
       }
        return movieInfoService.getAllMovieInfo().log();
    }


    @GetMapping("/movie/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> getMovie(@PathVariable("id") String id) {

        return movieInfoService.getMovieInfo(id)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PutMapping("/movie/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> updateMovie(@RequestBody MovieInfo updateMovieInfo, @PathVariable String id) {

        return movieInfoService.updateMovieInfo(updateMovieInfo,id)
                .map(ResponseEntity.ok()::body )
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();


    }

    @DeleteMapping("/movie/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovie( @PathVariable String id) {

        return movieInfoService.deleteMovieInfo(id).log();
    }




}
























