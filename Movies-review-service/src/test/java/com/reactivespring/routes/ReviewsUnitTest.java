package com.reactivespring.routes;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.model.Review;
import com.reactivespring.repository.ReviewReactiveRepossitory;
import com.reactivespring.router.ReviewRouter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@AutoConfigureWebTestClient
@ContextConfiguration(classes = { ReviewRouter.class, ReviewHandler.class })
public class ReviewsUnitTest {

	@MockBean
	ReviewReactiveRepossitory repo;

	@Autowired
	WebTestClient webTestClient;

	@Test
	void testAddReview() {

		var review = new Review(null, "1", "Awesome Movie", 9.0);

		when(repo.save(review)).thenReturn(Mono.just(new Review("abc", "1", "Awesome Movie", 9.0)));
		webTestClient
		.post()
		.uri("/v1/review")
		.bodyValue(review)
		.exchange()
		.expectStatus()
		.isCreated()
		.expectBody(Review.class)
		.consumeWith(reviewResponse -> {
			var savedReview = reviewResponse.getResponseBody();
			assert savedReview != null;
			assertNotNull(savedReview.getReviewId());
			});
	}
	
	 @Test
	    void getAllReviews() {
	        //given
	        var reviewList = List.of(
	                new Review(null, "1", "Awesome Movie", 9.0),
	                new Review(null, "1", "Awesome Movie1", 9.0),
	                new Review(null, "2", "Excellent Movie", 8.0));

	        when(repo.findAll()).thenReturn(Flux.fromIterable(reviewList));
	     

	        //when
	        webTestClient
	                .get()
	                .uri("/v1/review")
	                .exchange()
	                .expectStatus()
	                .is2xxSuccessful()
	                .expectBodyList(Review.class)
	                .value(reviews->{
	                	assertNotNull(reviews);
	        });
	                

	    }

}
