package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void testFlux(){
        //given

        //when
        Flux<String> nameFlux = fluxAndMonoGeneratorService.generateFlux();

        //then
        StepVerifier.create(nameFlux)
                //.expectNext("Shiva","Ram","Hanuman","Ganesha")
                //.expectNextCount(4)
                .expectNext("Shiva")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testMap(){


        Flux<String> nameFlux = fluxAndMonoGeneratorService.transFromByMap();

        StepVerifier.create(nameFlux)
                .expectNext("SHIVA","RAM")
                .expectNextCount(2)
                .verifyComplete();
    }

   @Test
    void testFluxImmutability(){
        Flux<String> nameFlux = fluxAndMonoGeneratorService.toDemonstrateFluxImmutability();

        StepVerifier.create(nameFlux)
                .expectNext("Shiva","Ram","Hanuman","Ganesha")
                .verifyComplete();
   }

   @Test
   //Test to filter String length greater than 3 from Flux
    void testFilter(){

        Flux<String> nameFlux = fluxAndMonoGeneratorService.transformByFilter(3);

        StepVerifier.create(nameFlux)
                .expectNext("Shiva","Hanuman","Ganesha")
                .verifyComplete();
   }

   @Test
    void testFlatMap(){

        Flux<String> nameFlux = fluxAndMonoGeneratorService.transformByFlatMap();

        StepVerifier.create(nameFlux)
                .expectNext("S","h","i","v","a","R","a","m")
                .verifyComplete();
   }

   @Test
    void testFlatMapAsync(){

        //Chcek the console log you will not see Character of word printing in different order tha passed

       Flux<String> nameFlux = fluxAndMonoGeneratorService.transformByFlatMapAsync();

       StepVerifier.create(nameFlux)
               .expectNextCount(14)
               .verifyComplete();
   }

   @Test
    void testConcatMap(){

        Flux<String> nameFlux = fluxAndMonoGeneratorService.transformByFlatConcatMap();

        StepVerifier.create(nameFlux)
                .expectNext("S","h","i","v","a","R","a","m","B","e","t","t","e","r")
                .verifyComplete();
   }


   @Test
    void testMonoWithMap(){

       Mono<String> nameMono = fluxAndMonoGeneratorService.monoWithMap();

       StepVerifier.create(nameMono)
               .expectNext("SHIVA")
               .verifyComplete();
   }

    @Test
    void testMonoWithFilter(){

        Mono<String> nameMono = fluxAndMonoGeneratorService.monoWithMapANdFilter(2);

        StepVerifier.create(nameMono)
                .expectNext("SHIVA")
                .verifyComplete();

        Mono<String> nameMono1 = fluxAndMonoGeneratorService.monoWithMapANdFilter(6);

        StepVerifier.create(nameMono1)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testMonoFlatMap(){

        Mono<List<String>> mono = fluxAndMonoGeneratorService.monoWithFlatMap();

        StepVerifier.create(mono)
                .expectNext(List.of("S","H","I","V","A"))
                .verifyComplete();
    }

    @Test
    void testMonoFlatMapMany(){

        Flux<String> mono = fluxAndMonoGeneratorService.monoWithFlatMapMany();

        StepVerifier.create(mono)
                .expectNext("S","H","I","V","A")
                .verifyComplete();
    }

    @Test
    void testTransform(){

        Flux<String> mono = fluxAndMonoGeneratorService.transformExample(5);

        StepVerifier.create(mono)
                .expectNext("H","A","N","U","M","A","N","G","A","N","E","S","H","A")
                .verifyComplete();
    }

    @Test
    void testEmpty(){

        Flux<String> mono = fluxAndMonoGeneratorService.toCheckEmptyCondition(100);

        StepVerifier.create(mono)
                .expectNext("No Value: May be length you passed is greater tha all string")
                .verifyComplete();
    }

    @Test
    void testEmpty1(){

        Flux<String> mono = fluxAndMonoGeneratorService.toCheckEmptyCondition1(100);

        StepVerifier.create(mono)
                .expectNext("Empty")
                .verifyComplete();
    }

    @Test
    void testConcat(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreConcat();

        StepVerifier.create(fluxConcat)
                .expectNext("A","B","C","X","Y","Z")
                .verifyComplete();
    }

    @Test
    void testConcatWith(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreConcatWith();

        StepVerifier.create(fluxConcat)
                .expectNext("A","B","C")
                .verifyComplete();
    }

    @Test
    void testMerge(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreMerge();

        StepVerifier.create(fluxConcat)
                .expectNext("A","X","B","Y","C","Z")
                .verifyComplete();
    }

    @Test
    void testMergeWith_FLUX(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreMergeWith_FLUX();

        StepVerifier.create(fluxConcat)
                .expectNext("A","X","D","B","Y","E","C","Z")
                .verifyComplete();
    }

    @Test
    void testMergeWith_MONO(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreMergeWith_MONO();

        StepVerifier.create(fluxConcat)
                .expectNext("A","B","C")
                .verifyComplete();
    }


    @Test
    void testMergeWithSequantail(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreMerge_Sequantial();

        StepVerifier.create(fluxConcat)
                .expectNext("A","B","C","X","Y","Z")
                .verifyComplete();
    }


    @Test
    void testZip(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreZip();

        StepVerifier.create(fluxConcat)
                .expectNext("AX","BY","CZ")
                .verifyComplete();
    }

    @Test
    void testZip1(){

        Flux<String> fluxConcat = fluxAndMonoGeneratorService.exploreZip1();

        StepVerifier.create(fluxConcat)
                .expectNext("AX14","BY25","CZ36")
                .verifyComplete();
    }


    @Test
    void testZipWithMono(){

        Mono<String> monoZip = fluxAndMonoGeneratorService.exploreZipWith_MONO();

        StepVerifier.create(monoZip)
                .expectNext("AB")
                .verifyComplete();
    }


}