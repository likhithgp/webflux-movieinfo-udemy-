package com.reactivespring.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {

    @Test
    void sinkTest_reply() {
        // Given
        Sinks.Many<Integer> replySink = Sinks.many().replay().all();
        // When
        replySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Then
        Flux<Integer> intFlux =replySink.asFlux();
        intFlux.subscribe((i)-> System.out.println("Receiver-1: "+i));
        Flux<Integer> intFlux1 =replySink.asFlux();
        intFlux1.subscribe((i)-> System.out.println("Receiver-2: "+i));

        replySink.tryEmitNext(3);
        Flux<Integer> intFlux2 =replySink.asFlux();
        intFlux2.subscribe((i)-> System.out.println("Receiver-3: "+i));
    }

    @Test
    void sinkTest_multicast() {
        // Given
        Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
        // When
        multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Then
        Flux<Integer> intFlux =multicastSink.asFlux();

        intFlux.subscribe((i)-> System.out.println("Receiver-1: "+i));

        Flux<Integer> intFlux1 =multicastSink.asFlux();
        intFlux1.subscribe((i)-> System.out.println("Receiver-2: "+i));

        multicastSink.tryEmitNext(3);

        Flux<Integer> intFlux2 =multicastSink.asFlux();
        intFlux2.subscribe((i)-> System.out.println("Receiver-3: "+i));
    }


    @Test
    void sinkTest_unicast() {
        // Given
        Sinks.Many<Integer> unicast = Sinks.many().unicast().onBackpressureBuffer();
        // When
        unicast.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicast.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        // Then
        Flux<Integer> intFlux =unicast.asFlux();

        intFlux.subscribe((i)-> System.out.println("Receiver-1: "+i));

        Flux<Integer> intFlux1 =unicast.asFlux();
        intFlux1.subscribe((i)-> System.out.println("Receiver-2: "+i));

        unicast.tryEmitNext(3);

        Flux<Integer> intFlux2 =unicast.asFlux();
        intFlux2.subscribe((i)-> System.out.println("Receiver-3: "+i));
    }

}
