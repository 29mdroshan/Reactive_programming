package com.reactivespring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reactivespring.model.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class MovieInfoServiceImpl implements MovieInfoService {

	@Autowired
	MovieInfoRepository repo;
	
//	Instead of using autowire, this is called construct injection pattern
//	public MovieInfoServiceImpl (MovieInfoRepository movieInfoRepository) {
//		this.repo=movieInfoRepository;
//	}
	
	@Override
	public Mono<MovieInfo> addMovie(MovieInfo movie) {
		
		return repo.save(movie);
	}

	@Override
	public Flux<MovieInfo> getAllMovieInfo() {
		
		return repo.findAll();
	}

	@Override
	public Mono<MovieInfo> getByMovieId(String movieInfoId) {
		
		return repo.findById(movieInfoId);
	}

	@Override
	public Mono<MovieInfo> updateMovie(MovieInfo movie, String movieInfoId) {
		
		return repo.findById(movieInfoId).flatMap(i->{
			i.setCast(movie.getCast());
			i.setName(movie.getName());
			i.setRelease_date(movie.getRelease_date());
			i.setYear(movie.getYear());
			return repo.save(i);
		});
	}

	@Override
	public Mono<Void> deleteByMovieId(String movieInfoId) {
		
		return repo.deleteById(movieInfoId);
	}

	@Override
	public Flux<MovieInfo> getAllMoviesByYear(Integer year) {
		
		return repo.findByYear(year);
	}

}
