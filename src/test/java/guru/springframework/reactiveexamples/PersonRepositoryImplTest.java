package guru.springframework.reactiveexamples;

import guru.springframework.reactiveexamples.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository;

    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);

        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.subscribe( person -> assertTrue(person.getFirstName().equals("Michael")));
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.map(person -> {
            System.out.println(person.toString());

            return person.getFirstName();
        }).subscribe(firstName -> {
           assertTrue(firstName.equals("Michael"));
        });
    }

    @Test
    void testFindMonoPersonByIdNotFoundWithException() {

        final Integer id = 8;

        Mono<Person> personMono = personRepository.getById(id);
        StepVerifier.create(personMono).expectNextCount(0);
        personMono.doOnError(throwable -> {
           Mono.empty();}).subscribe( emptyPerson -> assertTrue(emptyPerson.equals(Mono.empty())));
     }


    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxToListMono() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> {
           list.forEach(person -> {
               System.out.println(person.toString());
           });
        });
    }

    @Test
    void testFindPersonById() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 3;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();

        personMono.doOnError(throwable -> {
            System.out.println("I went boom");
        }).onErrorReturn(Person.builder().id(id).build())
                .subscribe(person -> {
            System.out.println(person.toString());
        });
    }
}
































