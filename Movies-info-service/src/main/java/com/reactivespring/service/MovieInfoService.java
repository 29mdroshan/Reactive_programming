package com.reactivespring.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.reactivespring.model.MovieInfo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoService {

	public Mono<MovieInfo> addMovie(MovieInfo movie);
	
	public Flux<MovieInfo> getAllMovieInfo();
	
	public Mono<MovieInfo> getByMovieId(String movieInfoId);
	
	public Mono<MovieInfo> updateMovie(MovieInfo movie,String movieInfoId);
	
	public Mono<Void> deleteByMovieId(String movieInfoId);

	public Flux<MovieInfo> getAllMoviesByYear(Integer year);
}
