package com.interview.resource;

import com.interview.commands.CreatePersonCommand;
import com.interview.commands.UpdatePersonCommand;
import com.interview.commands.UpsertPersonCommand;
import com.interview.domain.dto.Email;
import com.interview.domain.dto.Person;
import com.interview.exceptions.PersonNotFoundException;
import com.interview.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Resource defining Person endpoints.
 */
@RestController
@RequestMapping(value = "/api/persons", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonResource {

    @Autowired
    private PersonService service;

    @GetMapping("/{id}")
    public Person findById(@PathVariable UUID id) {
        return service.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    @GetMapping("/email")
    public Person findByEmail(@RequestHeader("Email") Email email) {
        return service.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException(email));
    }

    @GetMapping
    public Page<Person> findAll(@RequestParam int pageNumber, @RequestParam int limit) {
        return service.findAll(PageRequest.of(pageNumber, limit));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person create(@RequestBody CreatePersonCommand command) {
        return service.create(command);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Person update(@RequestBody UpdatePersonCommand command) {
        return service.update(command)
                .orElseThrow(() -> new PersonNotFoundException(command.id()));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Person upsert(@RequestBody UpsertPersonCommand command) {
        return service.upsert(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
