package com.interview.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@DBUnit(caseSensitiveTableNames = false) // H2 default lower-case unquoted
class FlightControllerIT {

    @Autowired
    MockMvc mvc;

    private static final String BASE = "/api/v1/flight";
    private static final String USER = "admin";
    private static final String PASS = "admin123";

    @Test
    @DataSet(
            value = {"datasets/plane.yml",
                     "datasets/app-users.yml"},
            cleanBefore = true, cleanAfter = true
    )
    void create_get_update_search_delete_flow() throws Exception {
        // 1) Create
        String createBody = """
            {
              "code":"TM200",
              "departureAirport":"OTP",
              "arrivalAirport":"LHR",
              "departureTime":"2025-08-11T09:30:00",
              "arrivalTime":"2025-08-11T11:50:00",
              "status":"SCHEDULED",
              "availableSeats":150,
              "price":199.99,
              "currency":"EUR",
              "terminal":"T1",
              "gate":"A3",
              "planeId":1
            }
            """;

        String created = mvc.perform(post(BASE)
                                             .with(httpBasic(USER, PASS))
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(createBody))
                .andExpect(status().isOk()) // controller returns 200 on create
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("TM200"))
                .andReturn().getResponse().getContentAsString();

        int idStart = created.indexOf("\"id\":") + 5;
        int idEnd = created.indexOf(",", idStart);
        int flightId = Integer.parseInt(created.substring(idStart, idEnd).trim());

        // 2) Get by ID
        mvc.perform(get(BASE + "/" + flightId)
                            .with(httpBasic(USER, PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TM200"))
                .andExpect(jsonPath("$.departureAirport").value("OTP"))
                .andExpect(jsonPath("$.arrivalAirport").value("LHR"));

        // 3) Update
        String updateBody = """
            {
              "code":"TM200",
              "departureAirport":"OTP",
              "arrivalAirport":"LHR",
              "departureTime":"2025-08-11T10:00:00",
              "arrivalTime":"2025-08-11T12:15:00",
              "status":"SCHEDULED",
              "availableSeats":150,
              "price":219.49,
              "currency":"EUR",
              "terminal":"T1",
              "gate":"A4",
              "planeId":1
            }
            """;

        mvc.perform(put(BASE + "/" + flightId)
                            .with(httpBasic(USER, PASS))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gate").value("A4"))
                .andExpect(jsonPath("$.price").value(219.49));

        // 4) Search (no pagination)
        mvc.perform(get(BASE)
                            .with(httpBasic(USER, PASS))
                            .queryParam("departureAirport", "OTP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TM200"));

        // 5) Search (paginated)
        mvc.perform(get(BASE)
                            .with(httpBasic(USER, PASS))
                            .queryParam("departureAirport", "OTP")
                            .queryParam("page", "0")
                            .queryParam("size", "10")
                            .queryParam("sort", "departureTime,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value("TM200"));

        // 6) Delete
        mvc.perform(delete(BASE + "/" + flightId)
                            .with(httpBasic(USER, PASS)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DataSet(
            value = {"datasets/custom/flight/one-locked.yml",
                     "datasets/app-users.yml"},
            cleanBefore = true, cleanAfter = true
    )
    void update_seats_locked_whenScheduled_returns400() throws Exception {
        // Flight 201 is SCHEDULED with availableSeats 150
        String updateBody = """
            {
              "code":"LOCK1",
              "departureAirport":"OTP",
              "arrivalAirport":"LHR",
              "departureTime":"2025-08-11T09:30:00",
              "arrivalTime":"2025-08-11T11:50:00",
              "status":"SCHEDULED",
              "availableSeats":151,  // changed -> should fail
              "price":199.99,
              "currency":"EUR",
              "terminal":"T1",
              "gate":"A3",
              "planeId":1
            }
            """;

        mvc.perform(put(BASE + "/201")
                            .with(httpBasic(USER, PASS))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody))
                .andExpect(status().isBadRequest());
        // If your ExceptionAdvisor returns a specific JSON error structure,
        // add jsonPath checks here (e.g., error code/message fields).
    }

    @Test
    @DataSet(
            value = {"datasets/plane.yml", "datasets/flight.yml",
                     "datasets/app-users.yml"},
            cleanBefore = true, cleanAfter = true
    )
    void search_filters_and_pagination() throws Exception {
        // No pagination -> array
        mvc.perform(get(BASE)
                            .with(httpBasic(USER, PASS))
                            .queryParam("departureAirport", "OTP")
                            .queryParam("arrivalAirport", "LHR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").exists());

        // Paginated -> Page<FlightDto>
        mvc.perform(get(BASE)
                            .with(httpBasic(USER, PASS))
                            .queryParam("departureAirport", "OTP")
                            .queryParam("arrivalAirport", "LHR")
                            .queryParam("page", "0")
                            .queryParam("size", "1")
                            .queryParam("sort", "departureTime,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DataSet(
            value = {"datasets/plane.yml",
                     "datasets/app-users.yml"},
            cleanBefore = true, cleanAfter = true
    )
    void create_validation_error_missing_departureTime_returns400() throws Exception {
        String badBody = """
            {
              "code":"VAL100",
              "departureAirport":"OTP",
              "arrivalAirport":"LHR",
              "arrivalTime":"2025-08-11T11:50:00",
              "status":"SCHEDULED",
              "availableSeats":150,
              "price":199.99,
              "currency":"EUR",
              "terminal":"T1",
              "gate":"A3",
              "planeId":1
            }
            """;

        mvc.perform(post(BASE)
                            .with(httpBasic(USER, PASS))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(badBody))
                .andExpect(status().isBadRequest());
        // Optionally assert your standardized error payload with jsonPath
    }
}
