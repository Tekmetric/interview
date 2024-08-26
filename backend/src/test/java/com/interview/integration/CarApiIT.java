package com.interview.integration;

import com.interview.dto.CarCreateDTO;
import com.interview.entity.Car;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.sql.annotation.ExpectDelete;
import org.quickperf.sql.annotation.ExpectInsert;
import org.quickperf.sql.annotation.ExpectSelect;
import org.quickperf.sql.annotation.ExpectUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@QuickPerfTest
@Sql(statements = {
        "INSERT INTO CAR (id, name) VALUES (753, 'Dacia');",
        "INSERT INTO CAR (id, name) VALUES (754, 'ToyTest');"
})
@Sql(statements = {"DELETE FROM CAR"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CarApiIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @ExpectSelect
    void shouldReturnACarWhenDataIsAlreadyPersisted() {
        ResponseEntity<Car> response = restTemplate.getForEntity("/api/cars/754", Car.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var car = response.getBody();
        Assertions.assertNotNull(car);

        assertEquals("ToyTest", car.getName(), "car name matches one in data.sql");
        assertNull(car.getCreatedAt(), "we don't see created at");
        assertNull(car.getUpdatedAt(), "we don't see updated at");
        assertNull(car.getVersion(), "we don't see version");
    }

    @Test
    @ExpectInsert(3)
    void shouldCreateANewCar() {
        CarCreateDTO newCar = new CarCreateDTO("NewCar");
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity("/api/cars", newCar, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCar = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCar, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // when creating same name car, expect conflict status
        createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity("/api/cars", newCar, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        createResponse = restTemplate
                .withBasicAuth("testuser", "testpass")
                .postForEntity("/api/cars", new Car("othercar"), Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @ExpectSelect
    void shouldReturnAllCarsWhenListIsRequestedDefaultPagination() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/cars", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertNotNull(body);
        var json = JsonPath.parse(body);
        var car = json.read("$.content[0]", Car.class);
        assertEquals("Dacia", car.getName());
        assertEquals(753L, car.getId());
        assertTrue(json.read("$.last", Boolean.class), "there should be just one page");
        assertEquals(2, json.read("$.totalElements", int.class));
    }

    @Test
    @ExpectSelect(4)
    void shouldReturnAllCarsWhenListIsRequestedPaginated() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/cars?page=0&size=1&sort=name,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var body = response.getBody();
        assertNotNull(body);
        var json = JsonPath.parse(body);
        var car = json.read("$.content[0]", Car.class);
        assertEquals("Dacia", car.getName());
        assertEquals(753L, car.getId());
        assertFalse(json.read("$.last", Boolean.class));

        // next page
        response = restTemplate.getForEntity("/api/cars?page=1&size=1&sort=name,asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        body = response.getBody();
        assertNotNull(body);

        json = JsonPath.parse(body);
        car = json.read("$.content[0]", Car.class);
        assertEquals("ToyTest", car.getName());
        assertEquals(754L, car.getId());
        assertTrue(json.read("$.last", Boolean.class));
    }

    @Test
    @ExpectSelect(2)
    @ExpectUpdate
    void shouldUpdateAnExistingCar() {
        Car carUpdate = new Car(null, "UpdatedCar");
        HttpEntity<Car> request = new HttpEntity<>(carUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange("/api/cars/754", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        var res = restTemplate.getForEntity("/api/cars/754", Car.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        var car = res.getBody();
        Assertions.assertNotNull(car);

        assertEquals(carUpdate.getName(), car.getName(), "car name matches one we've updated");
    }

    @Test
    @ExpectSelect(2)
    @ExpectDelete
    void shouldDeleteAnExistingCar() {
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("testuser", "testpass")
                .exchange("/api/cars/754", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/api/cars/754", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}