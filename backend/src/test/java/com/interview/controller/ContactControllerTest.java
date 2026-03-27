package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.resource.controller.ContactController;
import com.interview.resource.entity.Contact;
import com.interview.resource.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactService service;

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
    void create_WithValidContact_ShouldReturn201() throws Exception {
        when(service.addContact(any(Contact.class))).thenReturn(contact);

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.email").value("email@mail.org"));

        verify(service, times(1)).addContact(any(Contact.class));
    }

    @Test
    void create_WithInvalidEmail_ShouldReturn400() throws Exception {
        Contact badContact = contact;
        badContact.setEmail("missingat.org");

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"));

        verify(service, never()).addContact(any(Contact.class));
    }

    @Test
    void create_WithInvalidPhoneNumber_ShouldReturn400() throws Exception {
        Contact invalidContact = contact;
        invalidContact.setPhone("12340");

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phone").value("Invalid phone number format"));
    }


    @Test
    void getById_WhenContactExists_ShouldReturn200() throws Exception {
        when(service.getContactById(contactId)).thenReturn(Optional.of(contact));

        mockMvc.perform(get("/contacts/{id}", contactId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName"))
                .andExpect(jsonPath("$.email").value("email@mail.org"));

        verify(service, times(1)).getContactById(contactId);
    }

    @Test
    void getById_WhenContactDoesNotExist_ShouldReturn404() throws Exception {
        when(service.getContactById(contactId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/contacts/{id}", contactId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).getContactById(contactId);
    }

    @Test
    void getAll_ShouldReturnListOfContactss() throws Exception {
        Contact contact2 = new Contact();
        contact2.setId(UUID.randomUUID());
        contact2.setFirstName("John");
        contact2.setLastName("Doe");
        contact2.setEmail("jdoe@mail.com");
        contact2.setPhone("1234567890");
        when(service.getContacts()).thenReturn(Arrays.asList(contact, contact2));

        mockMvc.perform(get("/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(service, times(1)).getContacts();
    }

    @Test
    void update_WithValidContacts_ShouldReturn200() throws Exception {
        Contact updatedContact = contact;
        updatedContact.setFirstName("updatedFirstName");
        updatedContact.setId(contactId);

        when(service.updateContact(eq(contactId), any(Contact.class))).thenReturn(updatedContact);

        mockMvc.perform(put("/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updatedFirstName"));

        verify(service, times(1)).updateContact(eq(contactId), any(Contact.class));
    }

    @Test
    void update_WithInvalidData_ShouldReturn400() throws Exception {
        Contact invalidContacts = new Contact();

        mockMvc.perform(put("/contacts/{id}", contactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContacts)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(service).deleteContact(contactId);

        mockMvc.perform(delete("/contacts/{id}", contactId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteContact(contactId);
    }
}
