package com.interview.resource;

import com.interview.model.Person;
import com.interview.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@RestController
public class PersonResource {
    private final PersonRepository repository;

    @Autowired
    public PersonResource(final PersonRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/persons/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable @NotBlank final UUID id) {
        return ResponseEntity.of(repository.findById(id));
    }

    @GetMapping("/api/persons")
    public ResponseEntity<Iterable<Person>> listPersons() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/api/persons")
    public ResponseEntity<Person> createPerson(@RequestBody final Person person) {
        person.setId(UUID.randomUUID());
        return ResponseEntity.ok(repository.save(person));
    }

    @PutMapping("/api/persons/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable final UUID id, @RequestBody final Person person) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.save(new Person(id, person.getName())));
    }

    @DeleteMapping("/api/persons/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable final UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}