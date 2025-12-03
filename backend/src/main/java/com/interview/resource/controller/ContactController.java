package com.interview.resource.controller;

import com.interview.resource.entity.Contact;
import com.interview.resource.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Contact> getContacts(){
        return contactService.getContacts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact addContact(@Valid @RequestBody Contact contact) {
        return contactService.addContact(contact);
    }

    @GetMapping(path = "/{id}")
    public Optional<Contact> getContactById(@PathVariable UUID id) {
        return contactService.getContactById(id);
    }

    @PutMapping(path = "/{id}")
    public Contact updateContact(@PathVariable UUID id, @Valid @RequestBody Contact contact) {
        return contactService.updateContact(id, contact);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable UUID id) {
        contactService.deleteContact(id);
    }
}
