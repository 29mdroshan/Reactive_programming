package com.reactivespring.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactivespring.model.Review;

import reactor.core.publisher.Flux;

public interface ReviewReactiveRepossitory extends ReactiveMongoRepository<Review, String> {

	Flux<Review> findAllByMovieInfoId(String movieId);

	Flux<Review> findByMovieInfoId(Optional<String> movieId);

	Flux<Review> findReviewsByMovieInfoId(String movieId);

}
