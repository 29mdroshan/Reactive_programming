package com.reactivespring.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.reactivespring.model.MovieInfo;

import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

	@Autowired
	MovieInfoRepository movieInfoRepository;

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
	void findAll() {

		var moviesFlux = movieInfoRepository.findAll().log();

		StepVerifier.create(moviesFlux).expectNextCount(3).verifyComplete();

	}

	@Test
	void findById() {

		var movieInfo = movieInfoRepository.findById("abc");

		StepVerifier.create(movieInfo).assertNext(movieInfo1 -> {
			assertEquals("Dark Knight Rises", movieInfo1.getName());
		});
	}

	@Test
	void saveMovieInfo() {

		var movieInfo = new MovieInfo(null, "Batman Begins1", 2005, List.of("Christian Bale", "Michael Cane"),
				LocalDate.parse("2005-06-15"));

		var savedMovieInfo = movieInfoRepository.save(movieInfo).log();

		StepVerifier.create(savedMovieInfo).assertNext(movieInfo1 -> {
			assertNotNull(movieInfo1.getMovieInfoId());
		});

	}

	@Test
	void updateMovieInfo() {

		var movieInfo = movieInfoRepository.findById("abc").block();
		movieInfo.setYear(2021);

		var savedMovieInfo = movieInfoRepository.save(movieInfo);

		StepVerifier.create(savedMovieInfo).assertNext(movieInfo1 -> {
			assertNotNull(movieInfo1.getMovieInfoId());
			assertEquals(2021, movieInfo1.getYear());
		});

	}
	
	
	@Test
    void deleteMovieInfo() {

        movieInfoRepository.deleteById("abc").block();

        var movieInfos = movieInfoRepository.findAll();

        StepVerifier.create(movieInfos)
                .expectNextCount(2)
                .verifyComplete();

    }

}
