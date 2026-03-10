package com.interview.api.controller;

import static com.interview.assertion.QueryAssert.assertThatQuery;
import static com.interview.fixture.VehicleDataFixture.CUSTOMER_1_ID;
import static com.interview.fixture.VehicleDataFixture.VEHICLE_1_ID;
import static com.interview.fixture.WorkOrderDataFixture.LABOR_1_ID;
import static com.interview.fixture.WorkOrderDataFixture.LABOR_2_ID;
import static com.interview.fixture.WorkOrderDataFixture.LABOR_3_ID;
import static com.interview.fixture.WorkOrderDataFixture.PART_1_ID;
import static com.interview.fixture.WorkOrderDataFixture.PART_2_ID;
import static com.interview.fixture.WorkOrderDataFixture.PART_3_ID;
import static com.interview.fixture.WorkOrderDataFixture.WORK_ORDER_1_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.repository.WorkOrderRepository;
import com.interview.repository.entity.WorkOrderEntity;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
@Sql({"/datasets/vehicle-data.sql", "/datasets/work-order-data.sql"})
class WorkOrderRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WorkOrderRepository workOrderRepository;

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
    void getAllWorkOrdersReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[
                            {"id":"00000000-0000-0000-0000-000000000021","customerId":"%1$s","vehicleId":"00000000-0000-0000-0000-000000000011"},
                            {"id":"00000000-0000-0000-0000-000000000022","customerId":"%1$s","vehicleId":"00000000-0000-0000-0000-000000000012"},
                            {"id":"00000000-0000-0000-0000-000000000023","customerId":"00000000-0000-0000-0000-000000000002","vehicleId":"00000000-0000-0000-0000-000000000013"},
                            {"id":"00000000-0000-0000-0000-000000000024","customerId":"00000000-0000-0000-0000-000000000003","vehicleId":"00000000-0000-0000-0000-000000000015"}
                        ],"page":{"totalElements":4}}"""
                        .formatted(CUSTOMER_1_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getAllWorkOrdersByCustomerIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders").param("customerId", CUSTOMER_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[
                            {"id":"00000000-0000-0000-0000-000000000021","customerId":"%1$s","vehicleId":"00000000-0000-0000-0000-000000000011"},
                            {"id":"00000000-0000-0000-0000-000000000022","customerId":"%1$s","vehicleId":"00000000-0000-0000-0000-000000000012"}
                        ],"page":{"totalElements":2}}"""
                        .formatted(CUSTOMER_1_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getAllWorkOrdersByVehicleIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders").param("vehicleId", VEHICLE_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[{"id":"%s","customerId":"%s","vehicleId":"%s"}],"page":{"totalElements":1}}"""
                        .formatted(WORK_ORDER_1_ID, CUSTOMER_1_ID, VEHICLE_1_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getAllWorkOrdersByCustomerIdAndVehicleIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders").param("customerId", CUSTOMER_1_ID).param("vehicleId", VEHICLE_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"content":[{"id":"%s","customerId":"%s","vehicleId":"%s"}],"page":{"totalElements":1}}"""
                        .formatted(WORK_ORDER_1_ID, CUSTOMER_1_ID, VEHICLE_1_ID)));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void getWorkOrderByIdReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders/{id}", WORK_ORDER_1_ID))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"id":"%s","customerId":"%s","vehicleId":"%s",\
                        "partLineItems":[
                            {"id":"%s","name":"Oil Filter","quantity":1,"partNumber":"00000000-0000-0000-0000-000000000101"},
                            {"id":"%s","name":"Synthetic Oil 5W-30","quantity":5,"partNumber":"00000000-0000-0000-0000-000000000102"},
                            {"id":"%s","name":"Drain Plug Gasket","quantity":1,"partNumber":"00000000-0000-0000-0000-000000000103"}
                        ],\
                        "laborLineItems":[
                            {"id":"%s","name":"Oil Change","quantity":1,"serviceCode":"00000000-0000-0000-0000-000000000201"},
                            {"id":"%s","name":"Multi-Point Inspection","quantity":1,"serviceCode":"00000000-0000-0000-0000-000000000202"},
                            {"id":"%s","name":"Fluid Top-Off","quantity":1,"serviceCode":"00000000-0000-0000-0000-000000000203"}
                        ]}"""
                        .formatted(
                                WORK_ORDER_1_ID, CUSTOMER_1_ID, VEHICLE_1_ID,
                                PART_1_ID, PART_2_ID, PART_3_ID,
                                LABOR_1_ID, LABOR_2_ID, LABOR_3_ID)));
        assertThatQuery(statistics).hasQueryCount(2).hasCollectionFetchCount(0).hasNoOtherOperations();
    }

    @Test
    void createWorkOrderReturnsCreated() throws Exception {
        prepareForQuery();
        final String body = mockMvc.perform(post("/work-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"scheduledStartDateTime":"2026-04-01T10:00:00Z","customerId":"%s","vehicleId":"%s"}"""
                                .formatted(CUSTOMER_1_ID, VEHICLE_1_ID)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        final UUID id = UUID.fromString(JsonPath.read(body, "$.id"));
        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasInsertCount(1).hasNoOtherOperations();

        final WorkOrderEntity persisted = workOrderRepository.findById(id).orElseThrow();
        assertThat(persisted.getScheduledStartDateTime()).isEqualTo(Instant.parse("2026-04-01T10:00:00Z"));
        assertThat(persisted.getCustomer().getId()).isEqualTo(UUID.fromString(CUSTOMER_1_ID));
        assertThat(persisted.getVehicle().getId()).isEqualTo(UUID.fromString(VEHICLE_1_ID));
    }

    @Test
    void updateWorkOrderReturnsOk() throws Exception {
        prepareForQuery();
        mockMvc.perform(put("/work-orders/{id}", WORK_ORDER_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"scheduledStartDateTime":"2026-05-01T14:00:00Z","customerId":"%s","vehicleId":"%s"}"""
                                .formatted(CUSTOMER_1_ID, VEHICLE_1_ID)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasUpdateCount(1).hasNoOtherOperations();

        final WorkOrderEntity updated = workOrderRepository.findById(UUID.fromString(WORK_ORDER_1_ID)).orElseThrow();
        assertThat(updated.getScheduledStartDateTime()).isEqualTo(Instant.parse("2026-05-01T14:00:00Z"));
    }

    @Test
    void deleteWorkOrderReturnsNoContent() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{id}", WORK_ORDER_1_ID))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasDeleteCount(7).hasCollectionFetchCount(2).hasNoOtherOperations();

        assertThat(workOrderRepository.findById(UUID.fromString(WORK_ORDER_1_ID))).isEmpty();
    }

    @Test
    void createPartLineItemReturnsCreated() throws Exception {
        prepareForQuery();
        final String body = mockMvc.perform(post("/work-orders/{workOrderId}/part-line-items", WORK_ORDER_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Brake Pad","quantity":2,"partNumber":"00000000-0000-0000-0000-000000000102"}"""))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        final UUID id = UUID.fromString(JsonPath.read(body, "$.id"));
        entityManager.flush();
        entityManager.clear();
        // collection fetch from addPartLineItem — acceptable, collection should be relatively small
        assertThatQuery(statistics).hasInsertCount(1).hasCollectionFetchCount(1).hasNoOtherOperations();

        final WorkOrderEntity workOrder = workOrderRepository.findByIdWithPartLineItems(UUID.fromString(WORK_ORDER_1_ID)).orElseThrow();
        assertThat(workOrder.getPartLineItems()).anyMatch(item ->
                item.getId().equals(id) && item.getName().equals("Brake Pad") && item.getQuantity() == 2);
    }

    @Test
    void deletePartLineItemReturnsNoContent() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{workOrderId}/part-line-items/{lineItemId}", WORK_ORDER_1_ID, PART_1_ID))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasQueryCount(1).hasDeleteCount(1).hasNoOtherOperations();

        final WorkOrderEntity workOrder = workOrderRepository.findByIdWithPartLineItems(UUID.fromString(WORK_ORDER_1_ID)).orElseThrow();
        assertThat(workOrder.getPartLineItems()).hasSize(2);
        assertThat(workOrder.getPartLineItems())
                .extracting("id")
                .doesNotContain(UUID.fromString(PART_1_ID));
    }

    @Test
    void createLaborLineItemReturnsCreated() throws Exception {
        prepareForQuery();
        final String body = mockMvc.perform(post("/work-orders/{workOrderId}/labor-line-items", WORK_ORDER_1_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Tire Rotation","quantity":1,"serviceCode":"00000000-0000-0000-0000-000000000202"}"""))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        final UUID id = UUID.fromString(JsonPath.read(body, "$.id"));
        entityManager.flush();
        entityManager.clear();
        // collection fetch from addLaborLineItem — acceptable, collection should be relatively small
        assertThatQuery(statistics).hasInsertCount(1).hasCollectionFetchCount(1).hasNoOtherOperations();

        final WorkOrderEntity workOrder = workOrderRepository.findByIdWithLaborLineItems(UUID.fromString(WORK_ORDER_1_ID)).orElseThrow();
        assertThat(workOrder.getLaborLineItems()).anyMatch(item ->
                item.getId().equals(id) && item.getName().equals("Tire Rotation") && item.getQuantity() == 1);
    }

    @Test
    void deleteLaborLineItemReturnsNoContent() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{workOrderId}/labor-line-items/{lineItemId}", WORK_ORDER_1_ID, LABOR_1_ID))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();
        assertThatQuery(statistics).hasQueryCount(1).hasDeleteCount(1).hasNoOtherOperations();

        final WorkOrderEntity workOrder = workOrderRepository.findByIdWithLaborLineItems(UUID.fromString(WORK_ORDER_1_ID)).orElseThrow();
        assertThat(workOrder.getLaborLineItems()).hasSize(2);
        assertThat(workOrder.getLaborLineItems())
                .extracting("id")
                .doesNotContain(UUID.fromString(LABOR_1_ID));
    }

    @Test
    void getWorkOrderByIdReturnsNotFoundForUnknownId() throws Exception {
        prepareForQuery();
        mockMvc.perform(get("/work-orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void updateWorkOrderReturnsNotFoundForUnknownId() throws Exception {
        prepareForQuery();
        mockMvc.perform(put("/work-orders/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"scheduledStartDateTime":"2026-05-01T14:00:00Z","customerId":"%s","vehicleId":"%s"}"""
                                .formatted(CUSTOMER_1_ID, VEHICLE_1_ID)))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasNoOtherOperations();
    }

    @Test
    void deleteWorkOrderReturnsNotFoundForUnknownId() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasNoOtherOperations();
    }

    @Test
    void createPartLineItemReturnsNotFoundForUnknownWorkOrder() throws Exception {
        prepareForQuery();
        mockMvc.perform(post("/work-orders/{workOrderId}/part-line-items", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Brake Pad","quantity":2,"partNumber":"00000000-0000-0000-0000-000000000102"}"""))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasNoOtherOperations();
    }

    @Test
    void deletePartLineItemReturnsNotFoundForUnknownWorkOrder() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{workOrderId}/part-line-items/{lineItemId}", UUID.randomUUID(), PART_1_ID))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void createLaborLineItemReturnsNotFoundForUnknownWorkOrder() throws Exception {
        prepareForQuery();
        mockMvc.perform(post("/work-orders/{workOrderId}/labor-line-items", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Tire Rotation","quantity":1,"serviceCode":"00000000-0000-0000-0000-000000000202"}"""))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasNoOtherOperations();
    }

    @Test
    void deleteLaborLineItemReturnsNotFoundForUnknownWorkOrder() throws Exception {
        prepareForQuery();
        mockMvc.perform(delete("/work-orders/{workOrderId}/labor-line-items/{lineItemId}", UUID.randomUUID(), LABOR_1_ID))
                .andExpect(status().isNotFound());
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }
}
