package com.interview.resource.service;

import com.interview.resource.entity.Contact;
import com.interview.resource.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService{
    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> getContacts(){
        return contactRepository.findAll();
    }

    public Optional<Contact> getContactById(UUID id) {

        return contactRepository.findById(id);

    }

    public Contact addContact(Contact contact){
        return contactRepository.save(contact);
    }

    public Contact updateContact(UUID id, Contact contact) {
        return contactRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(contact.getFirstName());
                    existing.setMiddleName(contact.getMiddleName());
                    existing.setLastName(contact.getLastName());
                    existing.setEmail(contact.getEmail());
                    existing.setPhone(contact.getPhone());
                    existing.setPhoneType(contact.getPhoneType());
                    return contactRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Issue updating contact, with id: " + id));
    }

    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
    }
}
