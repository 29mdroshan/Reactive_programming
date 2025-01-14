package com.reactivespring.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoController {

	@GetMapping(value="/flux",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Integer> flux(){
	
		return Flux.just(1,2,3,4,5,5).log();
	}
	
	@GetMapping(value="/mono")
	public Mono<String> mono(){
	
		return Mono.just("Hello Roshan").log();
	}
	
//	@GetMapping(value="/stream",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
//	public Flux<Long> Stream(){
//	
//		return Flux.interval(Duration.ofSeconds(1)).log();
//	}
}
