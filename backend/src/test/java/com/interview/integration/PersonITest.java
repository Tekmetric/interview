package com.interview.integration;

import com.interview.commands.CreatePersonCommand;
import com.interview.commands.UpdatePersonCommand;
import com.interview.commands.UpsertPersonCommand;
import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;
import com.interview.domain.dto.Person;
import com.interview.domain.dto.PersonPage;
import com.interview.repository.PersonRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.interview.constant.PersonConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonITest {

    @LocalServerPort
    private int port;
    private String API_ROOT;

    @Autowired
    private PersonRepository repository;

    @BeforeEach
    public void setUp() {
        API_ROOT = "http://localhost:" + port + "/api/persons";
        RestAssured.port = port;

        // clear table before each test
        repository.deleteAll();
    }

    private Response createPerson(CreatePersonCommand command) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(command)
                .post(API_ROOT);
    }

    private Response getPerson(UUID id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get(API_ROOT + "/" + id);
    }

    private Response getPersonByEmail(Email email) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Email", email.value())
                .get(API_ROOT + "/email");
    }

    private Response updatePerson(UpdatePersonCommand command) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(command)
                .patch(API_ROOT + "/" + command.id());
    }

    private Response upsertPerson(UpsertPersonCommand command) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(command)
                .put(API_ROOT);
    }

    private Response deletePerson(UUID id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete(API_ROOT + "/" + id);
    }

    private Response getAllPersons(Pageable pageable) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("pageNumber", pageable.getPageNumber())
                .queryParam("limit", pageable.getPageSize())
                .get(API_ROOT);
    }

    @Test
    public void basicCrud() {
        var personOnCreate = createWithAssertions();
        getWithAssertions(personOnCreate);
        updateWithAssertions(personOnCreate.id());
        deleteWithAssertions(personOnCreate.id());
    }

    // helper functions for `basicCrud`
    private Person createWithAssertions() {
        var createPersonCommand =
                new CreatePersonCommand(
                        EMAIL,
                        FIRST_NAME,
                        LAST_NAME,
                        PHONE_NUMBER,
                        ADDRESS
                );
        var response = createPerson(createPersonCommand);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        var personOnCreate = response.body().as(Person.class);
        assertThat(personOnCreate.id()).isNotNull();
        assertThat(personOnCreate.email()).isEqualTo(EMAIL);
        assertThat(personOnCreate.firstName()).isEqualTo(FIRST_NAME);
        assertThat(personOnCreate.lastName()).isEqualTo(LAST_NAME);
        assertThat(personOnCreate.phoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(personOnCreate.address()).isEqualTo(ADDRESS);

        return personOnCreate;
    }

    private void getWithAssertions(Person personOnCreate) {
        // get by ID
        var personId = personOnCreate.id();
        var response = getPerson(personId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        var personOnGet = response.body().as(Person.class);
        assertThat(personOnGet).isEqualTo(personOnCreate);

        // get by email
        response = getPersonByEmail(EMAIL);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        personOnGet = response.body().as(Person.class);
        assertThat(personOnGet).isEqualTo(personOnCreate);
    }

    private void updateWithAssertions(UUID personId) {
        var newAddress =
                new Address(
                        "US",
                        "06901",
                        "CT",
                        "Stamford",
                        List.of("888 Washington Blvd")
                );
        var updatePersonCommand =
                new UpdatePersonCommand(
                        personId,
                        "Jesty",
                        "Jesterson",
                        "8329309401",
                        newAddress
                );
        var response = updatePerson(updatePersonCommand);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        var personOnUpdate = response.body().as(Person.class);
        assertThat(personOnUpdate.id()).isEqualTo(personOnUpdate.id());
        assertThat(personOnUpdate.email()).isEqualTo(EMAIL);
        assertThat(personOnUpdate.firstName()).isEqualTo("Jesty");
        assertThat(personOnUpdate.lastName()).isEqualTo("Jesterson");
        assertThat(personOnUpdate.phoneNumber()).isEqualTo("8329309401");
        assertThat(personOnUpdate.address()).isEqualTo(newAddress);
    }

    private void deleteWithAssertions(UUID personId) {
        var response = deletePerson(personId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // repeat since delete is idempotent
        response = deletePerson(personId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // 404 when getting deleted Person
        response = getPerson(personId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEqualTo(String.format("Person of ID %s not found", personId));

        response = getPersonByEmail(EMAIL);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().asString()).isEqualTo(String.format("Person of email %s not found", EMAIL));
    }

    @Test
    void pagination() {
        var persons = new ArrayList<Person>();
        for (int i = 0; i < 50; i++) {
            var response =
                    createPerson(
                            new CreatePersonCommand(
                                    new Email(String.format("testy%d@testmetric.com", i)),
                                    FIRST_NAME,
                                    LAST_NAME + ' ' + i,
                                    PHONE_NUMBER,
                                    ADDRESS
                            )
                    );
            persons.add(response.as(Person.class));
        }

        var pageable = Pageable.ofSize(10);
        do {
            var response = getAllPersons(pageable);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            var page = response.as(PersonPage.class);
            var personsFromPage = page.get().toList();
            assertThat(personsFromPage).isEqualTo(persons.subList(0, personsFromPage.size()));

            persons.removeAll(personsFromPage);

            pageable = page.nextPageable();
        } while (pageable.isPaged());

        assertThat(persons).isEmpty();
    }

    @Nested
    class Create {
        @Test
        void duplicateEmail() {
            var createPersonCommand =
                    new CreatePersonCommand(
                            EMAIL,
                            FIRST_NAME,
                            LAST_NAME,
                            PHONE_NUMBER,
                            ADDRESS
                    );

            // create person
            var response = createPerson(createPersonCommand);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

            // try to create with duplicate email
            response = createPerson(createPersonCommand);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo(String.format("Email %s is already in use", EMAIL));
        }
    }

    @Nested
    class Update {
        @Test
        void doesNotExist() {
            var badId = UUID.randomUUID();
            var response =
                    updatePerson(
                            new UpdatePersonCommand(
                                    badId,
                                    FIRST_NAME,
                                    LAST_NAME,
                                    PHONE_NUMBER,
                                    ADDRESS
                            )
                    );
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.body().asString()).isEqualTo(String.format("Person of ID %s not found", badId));
        }

        @Test
        void noChangesRequired() {
            var existing = createWithAssertions();
            UpdatePersonCommand command =
                    new UpdatePersonCommand(
                            existing.id(),
                            FIRST_NAME,
                            LAST_NAME,
                            PHONE_NUMBER,
                            ADDRESS
                    );

            var response = updatePerson(command);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            var personOnUpdate = response.body().as(Person.class);
            assertThat(personOnUpdate).isEqualTo(existing);
        }

        @Test
        void nullValuesProvided() {
            var existing = createWithAssertions();
            UpdatePersonCommand command =
                    new UpdatePersonCommand(
                            existing.id(),
                            null,
                            null,
                            null,
                            null
                    );

            var response = updatePerson(command);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            var personOnUpdate = response.body().as(Person.class);
            assertThat(personOnUpdate).isEqualTo(existing);
        }
    }

    @Nested
    class Upsert {
        UpsertPersonCommand command =
                new UpsertPersonCommand(
                        EMAIL,
                        FIRST_NAME,
                        LAST_NAME,
                        PHONE_NUMBER,
                        ADDRESS
                );

        @Test
        void createAndUpdate() {
            var response = upsertPerson(command);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            var personOnUpsert = response.body().as(Person.class);
            assertThat(personOnUpsert.id()).isNotNull();
            assertThat(personOnUpsert.email()).isEqualTo(EMAIL);
            assertThat(personOnUpsert.firstName()).isEqualTo(FIRST_NAME);
            assertThat(personOnUpsert.lastName()).isEqualTo(LAST_NAME);
            assertThat(personOnUpsert.phoneNumber()).isEqualTo(PHONE_NUMBER);
            assertThat(personOnUpsert.address()).isEqualTo(ADDRESS);

            var personId = personOnUpsert.id();
            var newAddress =
                    new Address(
                            "US",
                            "06901",
                            "CT",
                            "Stamford",
                            List.of("888 Washington Blvd")
                    );
            var updateCommand =
                    new UpsertPersonCommand(
                            EMAIL,
                            "Jesty",
                            "Jesterson",
                            "8329309401",
                            newAddress
                    );
            response = upsertPerson(updateCommand);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            personOnUpsert = response.body().as(Person.class);
            assertThat(personOnUpsert.id()).isEqualTo(personId);
            assertThat(personOnUpsert.email()).isEqualTo(EMAIL);
            assertThat(personOnUpsert.firstName()).isEqualTo("Jesty");
            assertThat(personOnUpsert.lastName()).isEqualTo("Jesterson");
            assertThat(personOnUpsert.phoneNumber()).isEqualTo("8329309401");
            assertThat(personOnUpsert.address()).isEqualTo(newAddress);
        }
    }
}
