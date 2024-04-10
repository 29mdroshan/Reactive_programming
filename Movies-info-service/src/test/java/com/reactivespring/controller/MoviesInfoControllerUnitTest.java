package com.reactivespring.controller;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactivespring.model.MovieInfo;
import com.reactivespring.service.MovieInfoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest {

	@Autowired
	WebTestClient webTestClient;
	
	@MockBean
	MovieInfoService service;
	
	@Test
	void testAddMovie() {
		MovieInfo movie=new MovieInfo("mockId","Bang Bang", 2005,List.of("hrithik roshan", "Katrina kafe"),LocalDate.parse("2005-06-15"));
		
		when(service.addMovie(movie)).thenReturn(Mono.just(movie));
		webTestClient.post()
		.uri("/v1/addMovie")
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isCreated()
		.expectBody(MovieInfo.class)
		.consumeWith(i->{
			var responseBody=i.getResponseBody();
			assert responseBody!=null;
			assert responseBody.getMovieInfoId()!=null;
			assertEquals("mockId", responseBody.getMovieInfoId());
		});
	}
	
	@Test
	void testAddMovie_validation() {
		MovieInfo movie=new MovieInfo("mockId","", -2005,List.of(""),LocalDate.parse("2005-06-15"));
		
		webTestClient.post()
		.uri("/v1/addMovie")
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isBadRequest()
		.expectBody(String.class)
		.consumeWith(i->{
		var responseBody=i.getResponseBody();
		System.out.println("*********");
		System.out.println(responseBody);
		assert responseBody!=null;
		assertEquals("movieInfo.cast must be a present,movieInfo.name must be present,movieInfo.year must be a Positve value", responseBody);
		
		});

	}

	@Test
	void testUpdateMovie() {
		MovieInfo movie=new MovieInfo("abc", "Dark Knight Rises1", 2013, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2012-07-20"));
		
		when(service.updateMovie(movie,"abc")).thenReturn(Mono.just(movie));
		webTestClient.put()
		.uri("/v1/updateMovie/abc")
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isCreated()
		.expectBody(MovieInfo.class)
		.consumeWith(i->{
			var movieInfo=i.getResponseBody();
			assert movieInfo!=null;
			assert movieInfo.getMovieInfoId()!=null;
			assertEquals("Dark Knight Rises1", movieInfo.getName());

		});
	}

	@Test
	void testGetAllMovieInfo() {
		
		var movieinfos = List.of(
				new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
						LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
						LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
						LocalDate.parse("2012-07-20")));
		
		when(service.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieinfos));
		
		webTestClient.get()
		.uri("/v1/getall")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(MovieInfo.class)
		.hasSize(3);
	}

	@Test
	void testGetByMovieId() {
		
		MovieInfo movie=new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2012-07-20"));
		
		when(service.getByMovieId("abc")).thenReturn(Mono.just(movie));
		webTestClient.get()
		.uri("/v1/getById/{movieInfoId}","abc")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBody(MovieInfo.class)
		.consumeWith(i->{
		var responseBody=i.getResponseBody();
		assertNotNull(responseBody);
		assertEquals("Dark Knight Rises", responseBody.getName());
		});
		
		 // assert
//	    StepVerifier.create(actual)
//	            .expectNext(TestingConstants.THE_DARK_KNIGHT_RISES)
//	            .verifyComplete();
//	    verify(mockService).getMovieInfoById(movieInfoId);
	}

	@Test
	void testDeleteByMovieId() {
		when(service.deleteByMovieId("abc")).thenReturn(Mono.empty());
		
		webTestClient.delete()
		.uri("/v1/deleteById/abc")
		.exchange()
		.expectStatus()
		.isNoContent();
	}

}
