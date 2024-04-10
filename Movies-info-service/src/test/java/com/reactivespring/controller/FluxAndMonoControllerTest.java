package com.reactivespring.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)//it gives access to all the endpoints in FluxAndMonoController class
@AutoConfigureWebTestClient //this annotation is going to make sure that webtestClient instance is automatically injected into this class.
class FluxAndMonoControllerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Test
	void testFlux() {
		
		webTestClient
		.get()
		.uri("/flux")
		.exchange() //invoke the endpoint
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(Integer.class)
		.hasSize(6);
	}
	
	@Test
	void testFlux_approach2() {
		
	var flux=webTestClient
		.get()
		.uri("/flux")
		.exchange() //invoke the endpoint
		.expectStatus()
		.is2xxSuccessful()
		.returnResult(Integer.class)
		.getResponseBody();
	
	
	StepVerifier.create(flux)
	.expectNext(1,2)
	.expectNextCount(4)
	.verifyComplete();
	}
	
	@Test
	void testFlux_approach3() {
		
	webTestClient
		.get()
		.uri("/flux")
		.exchange() //invoke the endpoint
		.expectStatus()
		.is2xxSuccessful()
		.expectBodyList(Integer.class)
		.consumeWith(listEntityExchangeResult->{
			var responseBody=listEntityExchangeResult.getResponseBody();
			assert(Objects.requireNonNull(responseBody).size()==6);
			assert(responseBody.get(0)==1);//here Objects.requireNotNull is optional
		});
	
	}
	
	@Test
	void testMono() {
		
		webTestClient
		.get()
		.uri("/mono")
		.exchange() //invoke the endpoint
		.expectStatus()
		.is2xxSuccessful()
		.expectBody(String.class)
		.consumeWith(stringEntityExchangeResult->{
			var responseBody=stringEntityExchangeResult.getResponseBody();
			assertEquals("Hello Roshan", responseBody.toString());
		});
	}
	
	@Test
	void testStream() {
		
	var flux=webTestClient
		.get()
		.uri("/Stream")
		.exchange() //invoke the endpoint
		.expectStatus()
		.is2xxSuccessful()
		.returnResult(Long.class)
		.getResponseBody();
	
	
	StepVerifier.create(flux)
	.expectNext(0L,1L,2L,3L)
	.thenCancel()
	.verify();
	}
	
	
	
	

}
