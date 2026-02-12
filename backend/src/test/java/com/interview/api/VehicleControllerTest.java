package com.interview.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VehicleControllerTest extends AbstractControllerTest {

    private static final String CUSTOMER_STUB_BASE = "api/customer/request/stub/";

    @Override
    protected String getRequestStubBase() {
        return "api/vehicle/request/stub/";
    }

    @Override
    protected String getResponseExpectedBase() {
        return "api/vehicle/response/expected/";
    }

    private String createCustomerAndGetId() throws Exception {
        String body = loadJson(CUSTOMER_STUB_BASE + "create-valid.json");
        ResultActions result = post("/customers", body);
        return assertCreatedAndGetId(result);
    }

    private String createVehicleAndGetId(String customerId) throws Exception {
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerId);
        ResultActions result = post("/vehicles", body);
        return assertCreatedAndGetId(result);
    }

    @Test
    @DisplayName("Create vehicle should return 201 and body with id, plateNumber, model, customerId when request is valid")
    void create_valid_201() throws Exception {
        // GIVEN one customer and request body
        String customerId = createCustomerAndGetId();
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", customerId);
        // WHEN
        ResultActions result = post("/vehicles", body);
        // THEN
        MvcResult mvcResult = result.andExpect(status().isCreated()).andReturn();
        String id = extractIdFromResponse(mvcResult.getResponse().getContentAsString());
        String expected = loadExpected("create-201.json").replace("__ID__", id).replace("__CUSTOMER_ID__", customerId);
        content().json(expected, false).match(mvcResult);
    }

    @Test
    @DisplayName("Create vehicle should return 400 and validation errors when plate number is blank")
    void create_blank_plate_400() throws Exception {
        // GIVEN invalid request (blank plate) and one customer
        String customerId = createCustomerAndGetId();
        String body = loadRequest("create-blank-plate.json").replace("__CUSTOMER_ID__", customerId);
        String expected = loadExpected("error-400-validation-plate.json");
        // WHEN
        ResultActions result = post("/vehicles", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create vehicle should return 400 and validation errors when model is blank")
    void create_blank_model_400() throws Exception {
        // GIVEN invalid request (blank model) and one customer
        String customerId = createCustomerAndGetId();
        String body = loadRequest("create-blank-model.json").replace("__CUSTOMER_ID__", customerId);
        String expected = loadExpected("error-400-validation-model.json");
        // WHEN
        ResultActions result = post("/vehicles", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create vehicle should return 400 and validation errors when customerId is null")
    void create_null_customerId_400() throws Exception {
        // GIVEN invalid request (null customerId)
        String body = loadRequest("create-null-customer-id.json");
        String expected = loadExpected("error-400-validation-customer-id.json");
        // WHEN
        ResultActions result = post("/vehicles", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Create vehicle should return 400 when customerId does not exist")
    void create_nonexistent_customerId_400() throws Exception {
        // GIVEN valid request body but nonexistent customerId
        UUID nonexistentCustomerId = UUID.randomUUID();
        String body = loadRequest("create-valid.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        String expected = loadExpected("error-400-reference-customer.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        // WHEN
        ResultActions result = post("/vehicles", body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Update vehicle should return 400 when customerId does not exist")
    void update_nonexistent_customerId_400() throws Exception {
        // GIVEN one customer and one vehicle, update with nonexistent customerId
        String customerId = createCustomerAndGetId();
        String id = createVehicleAndGetId(customerId);
        UUID nonexistentCustomerId = UUID.randomUUID();
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        String expected = loadExpected("error-400-reference-customer.json").replace("__CUSTOMER_ID__", nonexistentCustomerId.toString());
        // WHEN
        ResultActions result = put("/vehicles/" + id, body);
        // THEN
        assertResponse(result, status().isBadRequest(), expected);
    }

    @Test
    @DisplayName("Get vehicle by id should return 200 and body when vehicle exists")
    void getById_exists_200() throws Exception {
        // GIVEN one customer and one vehicle
        String customerId = createCustomerAndGetId();
        String id = createVehicleAndGetId(customerId);
        String expected = loadExpected("get-200.json").replace("__ID__", id).replace("__CUSTOMER_ID__", customerId);
        // WHEN
        ResultActions result = get("/vehicles/" + id);
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("Get vehicle by id should return 404 and error message when vehicle does not exist")
    void getById_notFound_404() throws Exception {
        // GIVEN nonexistent id
        UUID id = UUID.randomUUID();
        String expected = loadExpected("error-404.json").replace("__ID__", id.toString());
        // WHEN
        ResultActions result = get("/vehicles/" + id);
        // THEN
        assertResponse(result, status().isNotFound(), expected);
    }

    @Test
    @DisplayName("List vehicles should return 200 and empty array when no vehicles exist")
    void list_empty_200() throws Exception {
        // GIVEN one customer (no vehicles)
        createCustomerAndGetId();
        String expected = loadExpected("list-empty.json");
        // WHEN
        ResultActions result = get("/vehicles");
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("List vehicles should return 200 and non-empty array when vehicles exist")
    void list_hasItems_200() throws Exception {
        // GIVEN one customer and one vehicle
        String customerId = createCustomerAndGetId();
        String id = createVehicleAndGetId(customerId);
        String expected = loadExpected("list-one-item.json").replace("__ID__", id).replace("__CUSTOMER_ID__", customerId);
        // WHEN
        ResultActions result = get("/vehicles");
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("Update vehicle should return 200 and updated body when vehicle exists and request is valid")
    void update_valid_200() throws Exception {
        // GIVEN one customer and one vehicle
        String customerId = createCustomerAndGetId();
        String id = createVehicleAndGetId(customerId);
        String updateBody = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerId);
        String expected = loadExpected("update-200.json").replace("__ID__", id).replace("__CUSTOMER_ID__", customerId);
        // WHEN
        ResultActions result = put("/vehicles/" + id, updateBody);
        // THEN
        assertResponse(result, status().isOk(), expected);
    }

    @Test
    @DisplayName("Update vehicle should return 404 when vehicle does not exist")
    void update_notFound_404() throws Exception {
        // GIVEN nonexistent id and one customer
        UUID id = UUID.randomUUID();
        String customerId = createCustomerAndGetId();
        String body = loadRequest("update-valid.json").replace("__CUSTOMER_ID__", customerId);
        String expected = loadExpected("error-404.json").replace("__ID__", id.toString());
        // WHEN
        ResultActions result = put("/vehicles/" + id, body);
        // THEN
        assertResponse(result, status().isNotFound(), expected);
    }

    @Test
    @DisplayName("Delete vehicle should return 204 when vehicle exists")
    void delete_exists_204() throws Exception {
        // GIVEN one customer and one vehicle
        String customerId = createCustomerAndGetId();
        String id = createVehicleAndGetId(customerId);
        // WHEN
        ResultActions result = delete("/vehicles/" + id);
        // THEN
        assertResponse(result, status().isNoContent(), null);
    }

    @Test
    @DisplayName("Delete vehicle should return 204 when vehicle does not exist (idempotent)")
    void delete_notExists_204_idempotent() throws Exception {
        // GIVEN nonexistent id
        UUID id = UUID.randomUUID();
        // WHEN
        ResultActions result = delete("/vehicles/" + id);
        // THEN
        assertResponse(result, status().isNoContent(), null);
    }

    @Test
    @DisplayName("Delete vehicle should return 409 when vehicle is referenced by work order")
    void delete_referenced_409() throws Exception {
        // GIVEN one customer, one vehicle and one work order referencing the vehicle
        String customerId = createCustomerAndGetId();
        String vehicleId = createVehicleAndGetId(customerId);
        String workOrderBody = "{\"customerId\":\"" + customerId + "\",\"vehicleId\":\"" + vehicleId + "\",\"description\":\"Oil change\"}";
        post("/work-orders", workOrderBody);
        String expected = loadExpected("error-409-referenced.json");
        // WHEN
        ResultActions result = delete("/vehicles/" + vehicleId);
        // THEN
        assertResponse(result, status().isConflict(), expected);
    }
}
