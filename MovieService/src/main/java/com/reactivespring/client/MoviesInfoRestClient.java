package com.reactivespring.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import com.reactivespring.model.MovieInfo;
import com.reactivespring.model.Review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MoviesInfoRestClient {

	private WebClient webClient;

	public MoviesInfoRestClient(WebClient webClient) {
		this.webClient = webClient;
	}

	@Value("${restClient.moviesInfoUrl}")
	String moviesURl;

	public Flux<MovieInfo> retrieveMovieInfo(String movieId) {
//		return webClient
//				.get()
//				.uri("http://localhost:9100/v1/getById/Pathaan2523")
//				.retrieve()
//				.onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
//                   
//                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
//                        return Mono.error(new MoviesInfoClientException("There is no MovieInfo available for the passed in Id : " + movieId, clientResponse.statusCode().value()));
//                    }
//                    return clientResponse.bodyToMono(String.class)
//                            .flatMap(response -> Mono.error(new MoviesInfoClientException(response, clientResponse.statusCode().value())));
//                }))
//				.onStatus(HttpStatusCode::is5xxServerError, clientResponse->{
//					return clientResponse.bodyToMono(String.class)
//							.flatMap(responseMessage->Mono.error(new MoviesInfoServerException("Server Exception in MoviesInfoService "+responseMessage)));
//				})
//				.bodyToMono(MovieInfo.class)
//				.log();
				
				return webClient
						.get()
//						.uri(url)
						.uri("http://localhost:9100/v1/getById/" + movieId)
						.retrieve()
						
						.bodyToFlux(MovieInfo.class)
						.log();
	}

}
