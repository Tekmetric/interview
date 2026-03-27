package com.interview.service;

import com.interview.resource.entity.Contact;
import com.interview.resource.repository.ContactRepository;
import com.interview.resource.service.ContactService;
import com.interview.resource.service.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository repository;

    @InjectMocks
    private ContactService service = new ContactServiceImpl();

    private Contact contact;
    private UUID contactId;

    @BeforeEach
    void setUp() {
        contactId = UUID.randomUUID();
        contact = new Contact();
        contact.setId(contactId);
        contact.setFirstName("firstName");
        contact.setLastName("lastName");
        contact.setEmail("email@mail.org");
        contact.setPhone("1234567890");
    }

    @Test
    void addContact_ShouldSaveContact() {
        when(repository.save(any(Contact.class))).thenReturn(contact);

        Contact result = service.addContact(contact);

        assertNotNull(result);
        assertEquals("firstName", result.getFirstName());
        verify(repository, times(1)).save(contact);
    }

    @Test
    void getById_WhenContactsExists_ShouldReturnContacts() {
        when(repository.findById(contactId)).thenReturn(Optional.of(contact));

        Optional<Contact> result = service.getContactById(contactId);

        assertTrue(result.isPresent());
        assertEquals("firstName", result.get().getFirstName());
        verify(repository, times(1)).findById(contactId);
    }

    @Test
    void getById_WhenContactDoesNotExist_ShouldReturnEmpty() {
        when(repository.findById(contactId)).thenReturn(Optional.empty());

        Optional<Contact> result = service.getContactById(contactId);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(contactId);
    }

    @Test
    void getAll_ShouldReturnAllContacts() {
        Contact contact2 = new Contact();
        contact2.setId(UUID.randomUUID());
        contact2.setFirstName("John");
        contact2.setLastName("Doe");
        contact2.setEmail("j.doe@gmail.com");
        contact2.setPhone("1234567890");
        List<Contact> contacts = Arrays.asList(contact, contact2);
        when(repository.findAll()).thenReturn(contacts);

        List<Contact> result = service.getContacts();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void update_WhenContactExists_ShouldUpdateContact() {

        Contact updatedContact = new Contact();
        updatedContact.setFirstName("newFirstName");
        updatedContact.setLastName("newLastName");
        updatedContact.setEmail("newEmail@outlook.com");

        when(repository.findById(contactId)).thenReturn(Optional.of(contact));
        when(repository.save(any(Contact.class))).thenReturn(contact);

        Contact result = service.updateContact(contactId, updatedContact);

        assertNotNull(result);
        assertEquals("newFirstName", result.getFirstName());
        assertEquals("newEmail@outlook.com", result.getEmail());
        verify(repository, times(1)).findById(contactId);
        verify(repository, times(1)).save(any(Contact.class));
    }

    @Test
    void update_WhenContactDoesNotExist_ShouldThrowException() {
        when(repository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.updateContact(contactId, contact)
        );
        verify(repository, times(1)).findById(contactId);
        verify(repository, never()).save(any(Contact.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        doNothing().when(repository).deleteById(contactId);

        service.deleteContact(contactId);

        verify(repository, times(1)).deleteById(contactId);
    }
}
