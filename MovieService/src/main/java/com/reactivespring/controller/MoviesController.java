package com.reactivespring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.model.Movie;
import com.reactivespring.model.MovieInfo;
import com.reactivespring.model.Review;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {
	@Autowired
	MoviesInfoRestClient moviesInfoRestClient;

	@Autowired
	ReviewsRestClient reviewsRestClient;

//	@GetMapping(value="/{id}",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
//	public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId) {
//
//		return moviesInfoRestClient.retrieveMovieInfo(movieId)
//			.flatMap(moviesInfo -> {
//				var reviewsList = reviewsRestClient.retrieveReviews(movieId).collectList();
//
//				return reviewsList.map(reviews -> new Movie(moviesInfo, reviews));
//		});
//		
//	}
	
	@GetMapping("/{id}")
    public Flux<Movie> retrieveMovieById(@PathVariable("id") String movieId){

		return moviesInfoRestClient.retrieveMovieInfo(movieId)
				.flatMap(movieInfo->{
					 var reviewList = reviewsRestClient.retrieveReviews(movieId)
                           .collectList();
					 return reviewList.map(reviews -> new Movie(movieInfo, reviews));
				});


    }

   
}
