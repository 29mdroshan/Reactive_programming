package com.reactivespring.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;

class SinkTest {

	@Test
	void sink_replay() {
		Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
		
		replaySink.emitNext(1, EmitFailureHandler.FAIL_FAST);

		replaySink.emitNext(2, EmitFailureHandler.FAIL_FAST);

		Flux<Integer> intergerFlux=replaySink.asFlux();
		intergerFlux.subscribe(i->{
			System.out.println("Subscriber1 : "+i);
		});
		
		Flux<Integer> intergerFlux1=replaySink.asFlux();
		intergerFlux1.subscribe(i->{
			System.out.println("Subscriber2 : "+i);
		});
		
		replaySink.tryEmitNext(3);
		System.out.println("***");
	}
	
	@Test
	void sink_multicast() {
		Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
		
		multicastSink.emitNext(1, EmitFailureHandler.FAIL_FAST);

		multicastSink.emitNext(2, EmitFailureHandler.FAIL_FAST);

		Flux<Integer> intergerFlux=multicastSink.asFlux();
		intergerFlux.subscribe(i->{
			System.out.println("Subscriber1 : "+i);
		});
		
		Flux<Integer> intergerFlux1=multicastSink.asFlux();
		intergerFlux1.subscribe(i->{
			System.out.println("Subscriber2 : "+i);
		});
		
		multicastSink.emitNext(3, EmitFailureHandler.FAIL_FAST);
		System.out.println("***");
	}
	
	@Test
	void sink_unicast() {
		Sinks.Many<Integer> multicastSink = Sinks.many().unicast().onBackpressureBuffer();
		
		multicastSink.emitNext(1, EmitFailureHandler.FAIL_FAST);

		multicastSink.emitNext(2, EmitFailureHandler.FAIL_FAST);

		Flux<Integer> intergerFlux=multicastSink.asFlux();
		intergerFlux.subscribe(i->{
			System.out.println("Subscriber1 : "+i);
		});
		
		Flux<Integer> intergerFlux1=multicastSink.asFlux();
		intergerFlux1.subscribe(i->{
			System.out.println("Subscriber2 : "+i);
		});
		
		multicastSink.emitNext(3, EmitFailureHandler.FAIL_FAST);
		System.out.println("***");
	}

}
