package com.reactivespring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespring.model.MovieInfo;
import com.reactivespring.service.MovieInfoService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

	@Autowired
	private MovieInfoService service;
	
	Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().latest();
	
	
	@PostMapping("/addMovie")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<MovieInfo> addMovie(@RequestBody @Valid MovieInfo movie){
		return service.addMovie(movie)
				.doOnNext(savedInfo->movieInfoSink.tryEmitNext(savedInfo));
	}
	
	@PutMapping("/updateMovie/{movieInfoId}")
	public Mono<ResponseEntity<MovieInfo>> updateMovie(@RequestBody  MovieInfo movie,@PathVariable String movieInfoId){
		return service.updateMovie(movie,movieInfoId)
//				.map(i->{
//					return ResponseEntity.ok().body(i);
//				})
				.map(ResponseEntity.ok()::body)//changing Mono<MovieInfo> to Mono<ResponseEntity<MovieInfo>>
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
				.log();
	}
	
	
	@GetMapping(value="/getall",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<MovieInfo> getAllMovieInfo(){
		return service.getAllMovieInfo().log();
	}
	
	@GetMapping(value="/getallwithyear",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<MovieInfo> getAllMovieInfoWithYear(@RequestParam(value="year",required=false) Integer year){
		if(year != null) {
			return service.getAllMoviesByYear(year);
		}
		return service.getAllMovieInfo().log();
	}
	
	@GetMapping(value="/getById/{movieInfoId}",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Mono<ResponseEntity<MovieInfo>> getByMovieId(@PathVariable String movieInfoId){
		return service.getByMovieId(movieInfoId)
				.map(ResponseEntity.ok()::body)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
	}
	
	
	@GetMapping(value="/getById/stream",produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<MovieInfo> getByMovieIdSink(){
		return movieInfoSink.asFlux();
	}
	
	@DeleteMapping("/deleteById/{movieInfoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteByMovieId(@PathVariable String movieInfoId){
		return service.deleteByMovieId(movieInfoId);
	}
}
