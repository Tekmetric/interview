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

    public Contact updateContact(UUID id, Contact employee) {
        return contactRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(employee.getFirstName());

                    existing.setLastName(employee.getLastName());
                    existing.setEmail(employee.getEmail());
                    existing.setPhone(employee.getPhone());
                    existing.setPhoneType(employee.getPhoneType());
                    return contactRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Issue updating contact, with id: " + id));
    }

    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
    }
}
