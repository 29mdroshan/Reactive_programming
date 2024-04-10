package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.RouteMatcher.Route;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.handler.ReviewHandler;

import jakarta.validation.Path;

@Configuration
public class ReviewRouter {
	
	//RouterFunction is a way to configure different routes
	//serverResponse holds the response
	@Bean
	public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){

		
		return RouterFunctions.route()
			    .path("/v1/review", builder -> builder 
			        .GET("", request->reviewHandler.getReview(request))
			        .POST(request->reviewHandler.addReview(request))
			        .PUT("/{id}",request->reviewHandler.updateReview(request))
			        .DELETE("/{id}",reviewHandler::deleteReview)
			       
			        .GET("/getWithId",reviewHandler::getReviewWithMovieId)
			        
			       )
			    .build();
	}

}
