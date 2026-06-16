package com.interview.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkOrderControllerTest extends AbstractControllerTest {

    private static final String CUSTOMER_STUB_BASE = "api/customer/request/stub/";
    private static final String VEHICLE_STUB_BASE = "api/vehicle/request/stub/";

    @Override
    protected String getRequestStubBase() {
        return "api/work-order/request/stub/";
    }

    @Override
    protected String getResponseExpectedBase() {
        return "api/work-order/response/expected/";
    }

    private String replacePlaceholders(String json, String id, String customerId, String vehicleId, String createdAt) {
        return json.replace("__ID__", id).replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId).replace("__CREATED_AT__", createdAt);
    }

    private String createCustomerAndGetId() throws Exception {
        String body = loadJson(CUSTOMER_STUB_BASE + "create-valid.json");
        ResultActions result = post("/customers", body);
        return assertCreatedAndGetId(result);
    }

    private String createVehicleAndGetId(String customerId) throws Exception {
        String body = loadJson(VEHICLE_STUB_BASE + "create-valid.json").replace("__CUSTOMER_ID__", customerId);
        ResultActions result = post("/vehicles", body);
        return assertCreatedAndGetId(result);
    }

    private String createWorkOrderAndGetId(String customerId, String vehicleId) throws Exception {
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId);
        ResultActions result = post("/work-orders", body);
        return assertCreatedAndGetId(result);
    }

    @Test
    @DisplayName("Create work order should return 201 and body when request is valid")
    void create_valid_201() throws Exception {
        // GIVEN one customer and one vehicle
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId);
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        MvcResult mvcResult = result.andExpect(status().isCreated()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        String id = extractIdFromResponse(responseBody);
        String createdAt = extractFieldFromJson(responseBody, "createdAt");
        String expected = replacePlaceholders(loadExpected("create-201.json"), id, customerId, vehicleId, createdAt);
        content().json(expected, false).match(mvcResult);
    }

    @Test
    @DisplayName("Create work order should return 400 and validation errors when customerId is missing")
    void create_missing_customerId_400() throws Exception {
        // GIVEN one customer and one vehicle, invalid request (missing customerId)
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String body = loadRequest("create-missing-customer-id.json").replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-validation-customer-id.json");
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create work order should return 400 and validation errors when vehicleId is missing")
    void create_missing_vehicleId_400() throws Exception {
        // GIVEN one customer, invalid request (missing vehicleId)
        String customerId = createCustomerAndGetId();
        String body = loadRequest("create-missing-vehicle-id.json").replace("__CUSTOMER_ID__", customerId);
        String expected = loadExpected("error-400-validation-vehicle-id.json");
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create work order should return 400 when customerId does not exist")
    void create_nonexistent_customerId_400() throws Exception {
        // GIVEN one vehicle, request with nonexistent customerId
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        UUID nonexistentCustomerId = UUID.randomUUID();
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString()).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-reference-customer.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create work order should return 400 when vehicleId does not exist")
    void create_nonexistent_vehicleId_400() throws Exception {
        // GIVEN one customer, request with nonexistent vehicleId
        String customerId = createCustomerAndGetId();
        UUID nonexistentVehicleId = UUID.randomUUID();
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", nonexistentVehicleId.toString());
        String expected = loadExpected("error-400-reference-vehicle.json").replace("__VEHICLE_ID__", nonexistentVehicleId.toString());
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create work order should return 400 when vehicle does not belong to given customer")
    void create_vehicle_not_belong_to_customer_400() throws Exception {
        // GIVEN two customers, one vehicle for customer A; request work order with customer B and vehicle of A
        String customerA = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerA);
        String customerB = createCustomerAndGetId();
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerB).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-vehicle-not-belong-to-customer.json");
        // WHEN
        ResultActions result = post("/work-orders", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Get work order by id should return 200 and body when work order exists")
    void getById_exists_200() throws Exception {
        // GIVEN one customer, one vehicle and one work order
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        String expectedTemplate = loadExpected("get-200.json");
        // WHEN
        ResultActions result = get("/work-orders/" + id);
        // THEN
        MvcResult mvcResult = result.andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        String createdAt = extractFieldFromJson(responseBody, "createdAt");
        String expected = replacePlaceholders(expectedTemplate, id, customerId, vehicleId, createdAt);
        content().json(expected, false).match(mvcResult);
    }

    @Test
    @DisplayName("Get work order by id should return 404 and error message when work order does not exist")
    void getById_notFound_404() throws Exception {
        // GIVEN nonexistent id
        UUID id = UUID.randomUUID();
        String expected = loadExpected("error-404.json").replace("__ID__", id.toString());
        // WHEN
        ResultActions result = get("/work-orders/" + id);
        // THEN
        assertResponse(result, status().isNotFound(), expected);
    }

    @Test
    @DisplayName("List work orders should return 200 and empty array when no work orders exist")
    void list_empty_200() throws Exception {
        // GIVEN one customer and one vehicle (no work orders)
        String customerId = createCustomerAndGetId();
        createVehicleAndGetId(customerId);
        String expected = loadExpected("list-empty.json");
        // WHEN
        ResultActions result = get("/work-orders");
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("List work orders should return 200 and non-empty array when work orders exist")
    void list_hasItems_200() throws Exception {
        // GIVEN one customer, one vehicle and one work order
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        String responseBody = get("/work-orders/" + id).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String createdAt = extractFieldFromJson(responseBody, "createdAt");
        String expected = replacePlaceholders(loadExpected("list-one-item.json"), id, customerId, vehicleId, createdAt);
        // WHEN
        ResultActions result = get("/work-orders");
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("List work orders with status filter should return 200 and matching items")
    void list_withStatusFilter_200() throws Exception {
        // GIVEN one customer, one vehicle and one work order
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        String responseBody = get("/work-orders/" + id).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String createdAt = extractFieldFromJson(responseBody, "createdAt");
        String expected = replacePlaceholders(loadExpected("list-one-item.json"), id, customerId, vehicleId, createdAt);
        // WHEN
        ResultActions result = get("/work-orders?status=OPEN");
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("Update work order should return 200 and updated body when request is valid")
    void update_valid_200() throws Exception {
        // GIVEN one customer, one vehicle and one work order
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        String updateBody = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId);
        // WHEN
        ResultActions result = put("/work-orders/" + id, updateBody);
        // THEN
        MvcResult mvcResult = result.andExpect(status().isOk()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        String createdAt = extractFieldFromJson(responseBody, "createdAt");
        String expected = replacePlaceholders(loadExpected("update-200.json"), id, customerId, vehicleId, createdAt);
        content().json(expected, false).match(mvcResult);
    }

    @Test
    @DisplayName("Update work order should return 404 when work order does not exist")
    void update_notFound_404() throws Exception {
        // GIVEN nonexistent id, one customer and one vehicle
        UUID id = UUID.randomUUID();
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-404.json").replace("__ID__", id.toString());
        // WHEN
        ResultActions result = put("/work-orders/" + id, body);
        // THEN
        assertResponse(result, status().isNotFound(), expected);
    }

    @Test
    @DisplayName("Update work order should return 400 and validation errors when status is missing")
    void update_missing_status_400() throws Exception {
        // GIVEN one customer, one vehicle and one work order, invalid request (missing status)
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        String body = loadRequest("update-missing-status.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-validation-status.json");
        // WHEN
        ResultActions result = put("/work-orders/" + id, body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Update work order should return 400 when customerId does not exist")
    void update_nonexistent_customerId_400() throws Exception {
        // GIVEN one work order, update with nonexistent customerId
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        UUID nonexistentCustomerId = UUID.randomUUID();
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString()).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-reference-customer.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        // WHEN
        ResultActions result = put("/work-orders/" + id, body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Update work order should return 400 when vehicleId does not exist")
    void update_nonexistent_vehicleId_400() throws Exception {
        // GIVEN one work order, update with nonexistent vehicleId
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        UUID nonexistentVehicleId = UUID.randomUUID();
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerId).replace("__VEHICLE_ID__", nonexistentVehicleId.toString());
        String expected = loadExpected("error-400-reference-vehicle.json").replace("__VEHICLE_ID__", nonexistentVehicleId.toString());
        // WHEN
        ResultActions result = put("/work-orders/" + id, body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Update work order should return 400 when vehicle does not belong to given customer")
    void update_vehicle_not_belong_to_customer_400() throws Exception {
        // GIVEN two customers, one vehicle for customer A, one work order; update with customer B and vehicle of A
        String customerA = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerA);
        String customerB = createCustomerAndGetId();
        String id = createWorkOrderAndGetId(customerB, createVehicleAndGetId(customerB));
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerB).replace("__VEHICLE_ID__", vehicleId);
        String expected = loadExpected("error-400-vehicle-not-belong-to-customer.json");
        // WHEN
        ResultActions result = put("/work-orders/" + id, body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Delete work order should return 204 when work order exists")
    void delete_exists_204() throws Exception {
        // GIVEN one customer, one vehicle and one work order
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String id = createWorkOrderAndGetId(customerId, vehicleId);
        // WHEN
        ResultActions result = delete("/work-orders/" + id);
        // THEN
        assertResponse(result, status().isNoContent(), null);
    }

    @Test
    @DisplayName("Delete work order should return 204 when work order does not exist (idempotent)")
    void delete_notExists_204_idempotent() throws Exception {
        // GIVEN nonexistent id
        UUID id = UUID.randomUUID();
        // WHEN
        ResultActions result = delete("/work-orders/" + id);
        // THEN
        assertResponse(result, status().isNoContent(), null);
    }
}
