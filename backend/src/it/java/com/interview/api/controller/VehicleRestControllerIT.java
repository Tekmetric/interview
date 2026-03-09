package com.interview.api.controller;

import static com.interview.test.QueryAssert.assertThatQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.domain.Vin;
import com.interview.repository.VehicleRepository;
import com.interview.repository.entity.VehicleEntity;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql("/datasets/vehicle-data.sql")
class VehicleRestControllerIT {

    private static final String CUSTOMER_ID = "00000000-0000-0000-0000-000000000001";
    private static final String VEHICLE_ID = "00000000-0000-0000-0000-000000000011";
    private static final String VEHICLE_VIN = "JH4KA8260MC000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = sessionFactory.getStatistics();
    }

    private void prepareForQuery() {
        entityManager.flush();
        entityManager.clear();
        statistics.clear();
    }

    @Test
    void getAllVehiclesReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[{"id":"%s","vin":"%s","customerId":"%s"}],"page":{"totalElements":1}}"""
                        .formatted(VEHICLE_ID, VEHICLE_VIN, CUSTOMER_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getAllVehiclesByCustomerIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/vehicles").param("customerId", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[{"id":"%s","vin":"%s","customerId":"%s"}],"page":{"totalElements":1}}"""
                        .formatted(VEHICLE_ID, VEHICLE_VIN, CUSTOMER_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getVehicleByIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/vehicles/{id}", VEHICLE_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":"%s","vin":"%s","customerId":"%s"}"""
                        .formatted(VEHICLE_ID, VEHICLE_VIN, CUSTOMER_ID)));
        assertThatQuery(statistics).hasQueryCount(0).hasNoOtherOperations();
    }

    @Test
    void createVehicleReturnsCreated() throws Exception {
        prepareForQuery();
        final String body = mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vin":"1HGBH41JXMN109186","customerId":"%s"}"""
                                .formatted(CUSTOMER_ID)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        final UUID id = UUID.fromString(JsonPath.read(body, "$.id"));
        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasInsertCount(1).hasNoOtherOperations();

        final VehicleEntity persisted = vehicleRepository.findById(id).orElseThrow();
        assertThat(persisted.getVin()).isEqualTo(new Vin("1HGBH41JXMN109186"));
        assertThat(persisted.getCustomer().getId()).isEqualTo(UUID.fromString(CUSTOMER_ID));
    }

    @Test
    void updateVehicleReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(put("/vehicles/{id}", VEHICLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vin":"2HGBH41JXMN109186","customerId":"%s"}"""
                                .formatted(CUSTOMER_ID)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasUpdateCount(1).hasNoOtherOperations();

        final VehicleEntity updated = vehicleRepository.findById(UUID.fromString(VEHICLE_ID)).orElseThrow();
        assertThat(updated.getVin()).isEqualTo(new Vin("2HGBH41JXMN109186"));
    }

    @Test
    void deleteVehicleReturnsNoContent() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/vehicles/{id}", VEHICLE_ID))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        assertThat(vehicleRepository.findById(UUID.fromString(VEHICLE_ID))).isEmpty();
    }
}
