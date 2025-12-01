package com.interview.resource.service;

import com.interview.resource.entity.Contact;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
public interface ContactService {
    Flux<Contact> getContacts();
    Mono<Optional<Contact>> getContactById(UUID id);
    Mono<Object> addContact(Contact contact);
}
