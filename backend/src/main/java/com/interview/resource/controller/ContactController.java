package com.interview.resource.controller;

import com.interview.resource.entity.Contact;
import com.interview.resource.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Contact> getContacts(){
        return contactService.getContacts();
    }

    @PostMapping
    public Mono<Void> addContact(@RequestBody Contact contact) {
        contactService.addContact(contact);
        return Mono.empty();
    }

    @GetMapping(path = "/{id}")
    public Mono<Optional<Contact>> getContactById(@PathVariable UUID id) {
        Mono<Optional<Contact>> contact = contactService.getContactById(id);
        return contact;
    }
}
