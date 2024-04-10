package com.reactivespring.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import com.reactivespring.model.MovieInfo;
import com.reactivespring.model.Review;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class ReviewsRestClient {

	@Autowired
	private WebClient webClient;

	@Value("${restClient.reviewsUrl}")
	private String reviewsUrl;

	
	public Flux<Review> retrieveReviews(String movieId) {
		
		//to build query param url
		var url=UriComponentsBuilder.fromHttpUrl(reviewsUrl).queryParam("movieId", movieId).buildAndExpand().toString();
		return webClient
				.get()
				.uri(url)
//				.uri("http://localhost:9300/v1/review/getWithId/" + movieId)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
	                   
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(response -> Mono.error(new ReviewsClientException(response)));
                }))
				.onStatus(HttpStatusCode::is5xxServerError, clientResponse->{
					return clientResponse.bodyToMono(String.class)
							.flatMap(responseMessage->Mono.error(new ReviewsServerException("Server Exception in ReviewsService "+responseMessage)));
				})
				.bodyToFlux(Review.class)
				.log();
	}
}
