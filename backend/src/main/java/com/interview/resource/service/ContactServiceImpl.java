package com.interview.resource.service;

import com.interview.resource.entity.Contact;
import com.interview.resource.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService{
    @Autowired
    private ContactRepository contactRepository;

    public Flux<Contact> getContacts(){
        return Flux.fromIterable( contactRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Optional<Contact>> getContactById(UUID id) {
        Mono<Optional<Contact>> contact = Mono.fromCallable(() -> contactRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic());

        return contact.flatMap(c -> {
            if (c.isEmpty()) {
                throw new ResponseStatusException(404, "id not found",null);
            }
            return Mono.just(c);
        });

    }

    public Mono<Object> addContact(Contact contact){
        contactRepository.save(contact);
        return Mono.empty();
    }
}
