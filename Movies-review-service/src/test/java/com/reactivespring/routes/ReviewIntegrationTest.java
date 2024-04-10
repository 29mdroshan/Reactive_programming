package com.reactivespring.routes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


import com.reactivespring.model.Review;
import com.reactivespring.repository.ReviewReactiveRepossitory;




@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewIntegrationTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	ReviewReactiveRepossitory repo;
	
	@BeforeEach
    void setUp() {
		var reviewsList = List.of(
                new Review(null, "1", "Awesome Movie", 9.0),
                new Review(null, "1", "Awesome Movie1", 9.0),
                new Review("abc", "3", "Excellent Movie", 8.0));
        repo.saveAll(reviewsList)
                .blockLast();
    }
	
	 @AfterEach
	    void tearDown() {
	        repo.deleteAll()
	                .block();
	    }

	
	@Test
	void testAddReview() {
		
		 var review = new Review(null, "1", "Awesome Movie", 9.0);
	        //when
	        webTestClient
	                .post()
	                .uri("/v1/review")
	                .bodyValue(review)
	                .exchange()
	                .expectStatus().isCreated()
	                .expectBody(Review.class)
	                .consumeWith(reviewResponse -> {
	                    var savedReview = reviewResponse.getResponseBody();
	                    assert savedReview != null;
	                    assertNotNull(savedReview.getReviewId());
	                });
	}
	
	@Test
	void testAddReview_validation() {
		
		 var review = new Review(null, null, "Awesome Movie",- 9.0);
	        //when
	        webTestClient
	                .post()
	                .uri("/v1/review")
	                .bodyValue(review)
	                .exchange()
	                .expectStatus()
	                .isBadRequest()
	                .expectBody(String.class)
	                .isEqualTo("rating.movieInfoId : must not be null,rating.negative : please pass a non-negative value");
	}
	
	
	@Test
	void testGetAllReview() {

		webTestClient.get()
		.uri("/v1/review")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(Review.class)
		.hasSize(3);
	}
	
	@Test
	void testUpdateReview() {
		
		Review rev=new Review("abc", "3", "Excellent Movie", 7.0);
		webTestClient.put()
		.uri("/v1/review/abc")
		.bodyValue(rev)
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBody(Review.class)
		.consumeWith(i->{
			var review=i.getResponseBody();
			assert review!=null;
			assert review.getMovieInfoId()!=null;
			assertEquals(7.0, review.getRating());
			
		});
	}
	
	@Test
	void testUpdateReview_validate() {
		
		Review rev=new Review("abc", "3", "Excellent Movie", 7.0);
		webTestClient.put()
		.uri("/v1/review/xyz")
		.bodyValue(rev)
		.exchange()
		.expectStatus()
		.isNotFound()
//		.expectBody(String.class)
//		.isEqualTo("Review not found for the given review Id")
		;
	}
	
	
	@Test
	void testDeleteReviewById() {
		
		webTestClient.delete()
		.uri("/v1/review/abc")
		.exchange()
		.expectStatus()
		.isNoContent();
	}


}
