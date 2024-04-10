package com.reactivespring.handler;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.model.Review;
import com.reactivespring.repository.ReviewReactiveRepossitory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewHandler {
	
	@Autowired
	ReviewReactiveRepossitory repo;
	@Autowired
	Validator validator;

	public Mono<ServerResponse> addReview(ServerRequest request) {
		return request.bodyToMono(Review.class) //converting requestbody to  mono, basically giving access to request body
		.doOnNext(this::validate)
		.flatMap(review->{return repo.save(review);})
		.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);//converting Mono<Object> to Mono<ServerResponse>
//		.flatMap(savedReview->{
//			return ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview);
//		});
		
	}
	
	private void validate(Review review) {
		 var constrainViolation=validator.validate(review);
		 log.info("constrainViolation : {}",constrainViolation);
		 if(constrainViolation.size()>0) {
			 var errorMesage= constrainViolation.stream()
					 .map(ConstraintViolation::getMessage)
					 .sorted()
					 .collect(Collectors.joining(","));
			 throw new ReviewDataException(errorMesage);
		 }
		
		 
	}

	public Mono<ServerResponse> getReview(ServerRequest request) {
		var reviewFlux= repo.findAll();
		return ServerResponse.ok().body(reviewFlux, Review.class);
	}

	public Mono<ServerResponse> updateReview(ServerRequest request) {
		var reviewId=request.pathVariable("id");
		var existingReview=repo.findById(reviewId);
//				.switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given review Id")));
		
		
		 return existingReview
		.flatMap(
				review->request.bodyToMono(Review.class)//geting the review passed in serverRequest
				.map(reqReview->{
					review.setComment(reqReview.getComment()); //updating the existingting review with the review passed in http request
					review.setRating(reqReview.getRating()); //doubt it should be reversed
					return review;
				})
				.flatMap(repo::save)// saving updated review
				.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)//converting Mono<Object> to Mono<ServerResponse>
				)
		.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> deleteReview(ServerRequest request) {
		var reviewId=request.pathVariable("id");
		var existingReview=repo.findById(reviewId);
		return existingReview
				.flatMap(review->repo.deleteById(reviewId))
				.then(ServerResponse.noContent().build());
	}

	public Mono<ServerResponse> getReviewWithMovieId(ServerRequest request) {
		var movieInfoId = request.queryParam("movieId");
        if (movieInfoId.isPresent()) {
            var reviewFlux = repo.findAllByMovieInfoId(movieInfoId.get());
            return ServerResponse.ok().body(reviewFlux, Review.class);
        } 
        var reviewFlux= repo.findAll();
		return ServerResponse.ok().body(reviewFlux, Review.class);
       
	}
	
	

}
