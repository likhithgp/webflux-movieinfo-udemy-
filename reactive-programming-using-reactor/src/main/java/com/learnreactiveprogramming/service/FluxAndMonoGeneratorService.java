package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {

        FluxAndMonoGeneratorService demo = new FluxAndMonoGeneratorService();

        demo.generateFlux().subscribe(System.out::println);
        demo.generateMono().subscribe(name->{
            System.out.println("Mono data is "+name);
        });
        demo.transFromByMap().subscribe(System.out::println);


    }

    public Flux<String> generateFlux() {

        return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha")).log();
    }

    public Mono<String> generateMono() {

        return Mono.just("Shiva").log();
    }

    public Flux<String> transFromByMap(){

        //return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha")).map(String::toUppercase);
        return generateFlux().map(String::toUpperCase).log();
    }

    public Flux<String> toDemonstrateFluxImmutability(){

        Flux<String> flux = generateFlux();

        flux.map(String::toUpperCase).log();

        return flux;
    }

    public Flux<String> transformByFilter(int length){

        return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha"))
                .filter(str->str.length()>length)
                .log();
    }

    /**
     * Transfromation by flat Map
     */

    public Flux<String> transformByFlatMap(){

        return Flux.fromIterable(List.of("Shiva","Ram"))
                .flatMap(s->splitString(s))
                .log();

    }

    public Flux<String> transformByFlatMapAsync(){

        return Flux.fromIterable(List.of("Shiva","Ram","Better"))
                .flatMap(s->splitStringWithDely(s))
                .log();

    }

    /**
     * Concatmap is same as flatMap But concatMap preserve order
     * @return
     */
    public Flux<String> transformByFlatConcatMap(){

        return Flux.fromIterable(List.of("Shiva","Ram","Better"))
                .concatMap(s->splitStringWithDely(s))
                .log();

    }



    private Flux<String> splitString(String str){

        String[] charArray = str.split("");

        return Flux.fromArray(charArray);
    }

    private Flux<String> splitStringWithDely(String str){

        String[] charArray = str.split("");
        int dely = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(dely));
    }



    public Mono<String> monoWithMap(){

        return Mono.just("Shiva")
                .map(String::toUpperCase);
    }

    public Mono<String> monoWithMapANdFilter(int length){

        return Mono.just("Shiva")
                .map(String::toUpperCase)
                .filter(str->str.length()>length);
    }

    public Mono<List<String>> monoWithFlatMap(){

        return Mono.just("Shiva")
                .map(String::toUpperCase)
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String str){

        List<String> list = List.of(str.split(""));

        return Mono.just(list);
    }

    /**
     * FlatMapMany works same as flatMap but it return Flux;
     * @return
     */

    public Flux<String> monoWithFlatMapMany(){

        return Mono.just("Shiva")
                .map(String::toUpperCase)
                .flatMapMany(this::splitString)
                .log();
    }



    public Flux<String> transformExample(int length){

       Function<Flux<String>,Flux<String>> fluxMapAndFilter = repetaedOpteration -> repetaedOpteration.filter(s->s.length()>length)
               .map(String::toUpperCase);


        return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha"))
                .transform(fluxMapAndFilter)
                .flatMap(this::splitString)
                .log();
    }

    /**
     * DefaultIfEmpty && SwitchIFEmpty
     */
    public Flux<String> toCheckEmptyCondition(int length){

        //To Get empty value pass length greater than List of iterables
        Function<Flux<String>,Flux<String>> fluxMapAndFilter = repetaedOpteration -> repetaedOpteration.filter(s->s.length()>length)
                .map(String::toUpperCase);


        return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha"))
                .transform(fluxMapAndFilter)
                .flatMap(this::splitString)
                .defaultIfEmpty("No Value: May be length you passed is greater tha all string")
                .log();
    }

    public Flux<String> toCheckEmptyCondition1(int length){

        //To Get empty value pass length greater than List of iterables
        Function<Flux<String>,Flux<String>> fluxMapAndFilter = repetaedOpteration -> repetaedOpteration.filter(s->s.length()>length)
                .map(String::toUpperCase)
                .flatMap(this::splitString);

        Flux<String> switchParam = Flux.just("Empty");


        return Flux.fromIterable(List.of("Shiva","Ram","Hanuman","Ganesha"))
                .transform(fluxMapAndFilter)
                .switchIfEmpty(switchParam)
                .log();
    }


    /**
     * Combine Multiple Flux and Mono ---- concat and concatWith
     * concat and ConcatWIth subscribe both publisher in sequence
     */

    public Flux<String> exploreConcat(){

        Flux<String> abcFlux = Flux.just("A","B","C");

        Flux<String> xyzFlux = Flux.just("X","Y","Z");

        return Flux.concat(abcFlux,xyzFlux).log();
    }

    public Flux<String> exploreConcatWith(){

        Mono<String> aMono = Mono.just("A");

        Mono<String> bMono = Mono.just("B");

        Mono<String> cMono = Mono.just("C");

        return aMono.concatWith(bMono).concatWith(cMono).log();
    }

    /**
     * Merge and mergeWith use to combine two publisher into one
     *Here two publishers Subscribe at same time
     */
    public Flux<String> exploreMerge(){

        Flux<String> abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));

        Flux<String> xyzFlux = Flux.just("X","Y","Z")
                .delayElements(Duration.ofMillis(120));

        return Flux.merge(abcFlux,xyzFlux).log();
    }

    public Flux<String> exploreMergeWith_FLUX(){

        Flux<String> abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));

        Flux<String> xyzFlux = Flux.just("X","Y","Z")
                .delayElements(Duration.ofMillis(110));
        Flux<String> deFlux = Flux.just("D","E")
                .delayElements(Duration.ofMillis(120));

        return abcFlux.mergeWith(xyzFlux).mergeWith(deFlux).log();
    }

    public Flux<String> exploreMergeWith_MONO(){

        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        Mono<String> cMono = Mono.just("C");

        return aMono.mergeWith(bMono).mergeWith(cMono).log();
    }

    /**
     * mergeSequential same as merge But in sequence
     */

    public Flux<String> exploreMerge_Sequantial(){

        Flux<String> abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));

        Flux<String> xyzFlux = Flux.just("X","Y","Z")
                .delayElements(Duration.ofMillis(120));

        return Flux.mergeSequential(abcFlux,xyzFlux).log();
    }


    public Flux<String> exploreZip(){

        Flux<String> abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));

        Flux<String> xyzFlux = Flux.just("X","Y","Z")
                .delayElements(Duration.ofMillis(120));

        return Flux.zip(abcFlux,xyzFlux,(fromAbcFlux,fromXyzFlux)->fromAbcFlux+fromXyzFlux).log();
    }


    public Flux<String> exploreZip1(){

        Flux<String> abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));

        Flux<String> xyzFlux = Flux.just("X","Y","Z")
                .delayElements(Duration.ofMillis(120));

        Flux<String> flux123 = Flux.just("1","2","3")
                .delayElements(Duration.ofMillis(120));

        Flux<String> flux456 = Flux.just("4","5","6")
                .delayElements(Duration.ofMillis(120));

        return Flux.zip(abcFlux,xyzFlux,flux123,flux456)
                .map(t4->t4.getT1()+t4.getT2()+t4.getT3()+t4.getT4())
                .log();
    }

    public Mono<String> exploreZipWith_MONO(){

        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2-> t2.getT1()+t2.getT2())
                .log();
    }


}
