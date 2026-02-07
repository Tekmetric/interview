package com.interview.resource.service;

import com.interview.resource.entity.Contact;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface ContactService {
    List<Contact> getContacts();
    Optional<Contact> getContactById(UUID id);
    Contact addContact(Contact contact);
    Contact updateContact(UUID uuid, Contact contact);
    void deleteContact(UUID id);
}
