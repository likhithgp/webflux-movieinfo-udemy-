package com.reactivespring.moviesinfoservice.service;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repo.MovieInfoRepo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private MovieInfoRepo movieInfoRepo;

    public MovieInfoService(MovieInfoRepo movieInfoRepo) {
        this.movieInfoRepo = movieInfoRepo;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {

        return movieInfoRepo.save(movieInfo).log();
    }

    public Flux<MovieInfo> getAllMovieInfo() {

        return movieInfoRepo.findAll().log();
    }

    public Mono<MovieInfo> getMovieInfo(String id) {

        return movieInfoRepo.findById(id).log();
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updateMovieInfo, String id) {

      return   movieInfoRepo.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setCast(updateMovieInfo.getCast());
                    movieInfo.setName(updateMovieInfo.getName());
                    movieInfo.setYear(updateMovieInfo.getYear());
                    movieInfo.setRelease_date(updateMovieInfo.getRelease_date());
                    return movieInfoRepo.save(movieInfo).log();
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {

        return movieInfoRepo.deleteById(id);
    }

    public Flux<MovieInfo> findByYear(Integer year) {

        return movieInfoRepo.findByYear(year);
    }
}
