package com.reactivespring.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.reactivespring.model.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

	@Autowired
	MovieInfoRepository movieInfoRepository;

	@Autowired
	WebTestClient webTestClient;
	
	@BeforeEach
	void setUp() {

		var movieinfos = List.of(
				new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
						LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"),
						LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
						LocalDate.parse("2012-07-20")));

		movieInfoRepository.saveAll(movieinfos).blockLast();// makes sure it get completed before test case , used only
															// in testing if used
		// in actual call it gives error because you are blocking the thread
	}

	@AfterEach
	void tearDown() {
		movieInfoRepository.deleteAll().block();
	}

	@Test
	void testAddMovie() {
		
		
		MovieInfo movie=new MovieInfo(null,"Bang Bang", 2005,List.of("hrithik roshan", "Katrina kafe"),LocalDate.parse("2005-06-15"));
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
		});
	}
	
	@Test
	void testGetAllMovieInfo() {
		
		webTestClient.get()
		.uri("/v1/getall")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(MovieInfo.class)
		.hasSize(3);
	}
	
	@Test
	void testGetAllMovieInfoByYear() {
		
		var uri=UriComponentsBuilder.fromUriString("/v1/getallwithyear")
		.queryParam("year", 2005)
		.buildAndExpand().toUri();
		
		webTestClient.get()
		.uri(uri)
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(MovieInfo.class)
		.hasSize(1);
	}

	
	@Test
	void testGetMovieById() {
		
		webTestClient.get()
		.uri("/v1/getById/{movieInfoId}","abc")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBody(MovieInfo.class)
		.consumeWith(i->{
		var responseBody=i.getResponseBody();
		assertNotNull(responseBody);
		});
	}
	
	
	
	@Test
	void testGetMovieById_approach2() {
		
		webTestClient.get()
		.uri("/v1/getById/{movieInfoId}","abc")
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBody()
		.jsonPath("$.name").isEqualTo("Dark Knight Rises");
	}
	
	@Test
	void testGetMovieById_notFound() {
		
		webTestClient.get()
		.uri("/v1/getById/{movieInfoId}","xyz")
		.exchange()
		.expectStatus()
		.isNotFound();
	}
	
	@Test
	void testupdateMovie() {
		
		MovieInfo movie=new MovieInfo("abc", "Bang Bang", 2012, List.of("Hrithik roshan", "Katrina kafe"),
				LocalDate.parse("2012-07-20"));
		webTestClient.put()
		.uri("/v1/updateMovie/abc")
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.is2xxSuccessful()
		.expectBody(MovieInfo.class)
		.consumeWith(i->{
			var movieInfo=i.getResponseBody();
			assert movieInfo!=null;
			assert movieInfo.getMovieInfoId()!=null;
			assertEquals("Bang Bang", movieInfo.getName());
			
			assertEquals("Hrithik roshan",movieInfo.getCast().get(0) );
		});
	}
	
	
	
	
	@Test
	void testDeleteMovieById() {
		
		webTestClient.delete()
		.uri("/v1/deleteById/abc")
		.exchange()
		.expectStatus()
		.isNoContent();
	}

}
