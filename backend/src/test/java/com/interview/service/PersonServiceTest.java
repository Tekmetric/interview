package com.interview.service;

import com.interview.controller.Pagination;
import com.interview.dto.PersonDTO;
import com.interview.repository.PersonRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonServiceTest {

    private static final String EMAIL_ONE = "lugertnm@gmail.com";

    private final PersonService personService;

    private final PersonRepository personRepository;

    @Autowired
    PersonServiceTest(PersonService personService, PersonRepository personRepository) {
        this.personService = personService;
        this.personRepository = personRepository;
    }

    @Test
    @Order(1)
    public void createPersonTest() {

        PersonDTO personDTO = personService.savePerson(getTestPerson(EMAIL_ONE));

        assertTrue(personDTO.getId() > 0);

        //testing email unique constraint
        assertThrows(DataIntegrityViolationException.class,
                () -> personService.savePerson(getTestPerson(EMAIL_ONE)));

    }

    @Test
    @Order(2)
    public void findAndUpdatePersonTest() {

        List<PersonDTO> persons = personService.findAllPersons(new Pagination());

        assertEquals(1, persons.size());

        PersonDTO personDTO = personService.findPersonById(persons.get(0).getId()).get();

        assertEquals(EMAIL_ONE, personDTO.getEmail());
        assertEquals("The boy who lived", personDTO.getNickname());

        personDTO.setNickname("Quidditch Captain");
        personDTO.setPassword("password");
        personDTO.setNewPasswordOne("password!");
        personDTO.setNewPasswordTwo("password!");
        personService.savePerson(personDTO);

        assertEquals("password!", personRepository.findById(personDTO.getId()).get().getPassword());

        PersonDTO personDTO2 = personService.findPersonById(persons.get(0).getId()).get();
        assertEquals("Quidditch Captain", personDTO2.getNickname());

    }

    @Test
    @Order(3)
    public void deletePersonTest() {

        List<PersonDTO> persons = personService.findAllPersons(new Pagination());

        personService.deletePersonById(persons.get(0).getId());

        assertTrue(personService.findAllPersons(new Pagination()).size() == 0);

    }

    private PersonDTO getTestPerson(String email) {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setEmail(email);
        personDTO.setName("Harry Potter");
        personDTO.setState("UK");
        personDTO.setPassword("password");
        personDTO.setNickname("The boy who lived");

        return personDTO;

    }
}
