package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.fixtures.FruitFixture;
import com.interview.model.FruitCreateRequest;
import com.interview.model.FruitPatchRequest;
import com.interview.repository.FruitRepository;
import com.interview.service.FruitService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.interview.model.Fruit;

import java.util.stream.Stream;

import static com.interview.fixtures.FruitFixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FruitControllerTest {

    private static final String BASE_URL = "/api/v1/fruits";
    private static final Long FRUIT_ID = 7L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FruitService fruitService;

    @Autowired
    private FruitRepository fruitRepository;

    @BeforeEach
    void setUp() {
        fruitRepository.deleteAll();
    }

    @Test
    void testGetAllFruits() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        fruitService.create(fruitRequest);
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(FruitFixture.FRUIT_NAME_APPLE))
                .andExpect(jsonPath("$[0].color").value(FruitFixture.COLOR_RED))
                .andExpect(jsonPath("$[0].supplier").value(FruitFixture.SUPPLIER_A))
                .andExpect(jsonPath("$[0].batchNumber").value(FruitFixture.BATCH_NUMBER))
                .andExpect(jsonPath("$[0].originCountry").value(FruitFixture.COUNTRY))
                .andExpect(jsonPath("$[0].category").value(FruitFixture.CATEGORY))
                .andExpect(jsonPath("$[0].organic").value(true))
                .andExpect(jsonPath("$[0].quantity").value(FruitFixture.QUANTITY));
    }

    @Test
    void testGetAllFruits_empty() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetFruitById() throws Exception {
        // given
        var fruitRequest = FruitFixture.appleCreateRequest();
        var created = fruitService.create(fruitRequest);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FruitFixture.FRUIT_NAME_APPLE))
                .andExpect(jsonPath("$.color").value(FruitFixture.COLOR_RED))
                .andExpect(jsonPath("$.supplier").value(FruitFixture.SUPPLIER_A))
                .andExpect(jsonPath("$.batchNumber").value(FruitFixture.BATCH_NUMBER))
                .andExpect(jsonPath("$.originCountry").value(FruitFixture.COUNTRY))
                .andExpect(jsonPath("$.category").value(FruitFixture.CATEGORY))
                .andExpect(jsonPath("$.organic").value(true))
                .andExpect(jsonPath("$.quantity").value(FruitFixture.QUANTITY));
    }

    @Test
    void testGetFruitById_notFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + FRUIT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.NotFoundException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + FRUIT_ID))
                .andExpect(jsonPath("$.message").value("Fruit not found with id: " + FRUIT_ID))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateFruit() throws Exception {
        var req = FruitFixture.appleCreateRequest();
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    void testCreateFruit_duplicate() throws Exception {
        var req = FruitFixture.appleCreateRequest();
        fruitService.create(req);
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.BadRequestException"))
                .andExpect(jsonPath("$.path").value(BASE_URL))
                .andExpect(jsonPath("$.message").value("Fruit already exists with name: Apple, supplier: SupplierA, batch number: BATCH-001"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    void testCreateFruit_validationError(FruitCreateRequest req) throws Exception {
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exception").value("org.springframework.web.bind.MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.path").value(BASE_URL))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateFruit() throws Exception {
        var createReq = FruitFixture.appleCreateRequest();
        var created = fruitService.create(createReq);
        var updateReq = FruitFixture.appleUpdateRequest();

        mockMvc.perform(put(BASE_URL + "/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(COLOR_GREEN));
    }

    @Test
    void testUpdateFruit_notFound() throws Exception {
        var req = FruitFixture.appleCreateRequest();
        mockMvc.perform(put(BASE_URL + "/" + FRUIT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.NotFoundException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + FRUIT_ID))
                .andExpect(jsonPath("$.message").value("Fruit not found with id: " + FRUIT_ID))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testUpdateFruit_duplicate() throws Exception {
        var createReq = FruitFixture.appleCreateRequest();
        var created = fruitService.create(createReq);
        var anotherFruitRequest = FruitCreateRequest.builder()
                .name(FRUIT_NAME_BANANA)
                .supplier(SUPPLIER_A)
                .batchNumber(BATCH_NUMBER)
                .build();
        fruitService.create(anotherFruitRequest);

        var updateReq = createReq.toBuilder().name(FRUIT_NAME_BANANA).build();

        mockMvc.perform(put(BASE_URL + "/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.BadRequestException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + created.getId()))
                .andExpect(jsonPath("$.message").value("Fruit already exists with name: Banana, supplier: SupplierA, batch number: BATCH-001"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testPatchFruit() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        var created = fruitService.create(fruitRequest);

        var patchReq = FruitFixture.applePatchRequestGreen();
        mockMvc.perform(patch(BASE_URL + "/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value(COLOR_GREEN));
    }

    @Test
    void testPatchFruit_notFound() throws Exception {
        var patchReq = FruitFixture.applePatchRequestGreen();
        mockMvc.perform(patch(BASE_URL + "/" + FRUIT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchReq)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.NotFoundException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + FRUIT_ID))
                .andExpect(jsonPath("$.message").value("Fruit not found with id: " + FRUIT_ID))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testPatchFruit_duplicate() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        fruitService.create(fruitRequest);

        var anotherFruitRequest = FruitCreateRequest.builder()
                .name(FRUIT_NAME_BANANA)
                .supplier(SUPPLIER_A)
                .batchNumber(BATCH_NUMBER)
                .build();
        fruitService.create(anotherFruitRequest);

        var patchReq = FruitPatchRequest.builder()
                .name(FRUIT_NAME_BANANA)
                .color("yellow")
                .build();
        mockMvc.perform(patch(BASE_URL + "/" + FRUIT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.BadRequestException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + FRUIT_ID))
                .andExpect(jsonPath("$.message").value("Fruit already exists with name: Banana, supplier: SupplierA, batch number: BATCH-001"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteFruit() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        var created = fruitService.create(fruitRequest);

        mockMvc.perform(delete(BASE_URL + "/" + created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteFruit_notFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + FRUIT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.exception").value("com.interview.exception.NotFoundException"))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + FRUIT_ID))
                .andExpect(jsonPath("$.message").value("Fruit not found with id: " + FRUIT_ID))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetFruitsBySupplier() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        var anotherFruitRequest = fruitRequest.toBuilder().name(FRUIT_NAME_BANANA).build();
        fruitService.create(fruitRequest);
        fruitService.create(anotherFruitRequest);

        mockMvc.perform(get(BASE_URL + "/supplier/SupplierA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(FRUIT_NAME_APPLE))
                .andExpect(jsonPath("$[1].name").value(FRUIT_NAME_BANANA));
    }

    @Test
    void testGetFruitsBySupplier_empty() throws Exception {
        mockMvc.perform(get(BASE_URL + "/supplier/SupplierA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetFruitsByBatchNumberAndSupplier() throws Exception {
        var fruitRequest = FruitFixture.appleCreateRequest();
        fruitService.create(fruitRequest);
        mockMvc.perform(get(BASE_URL + "/supplier/SupplierA/batch/BATCH-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Apple"));
    }

    @Test
    void testGetFruitsByBatchNumberAndSupplier_empty() throws Exception {
        mockMvc.perform(get(BASE_URL + "/supplier/SupplierA/batch/BATCH-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetFruitsBySupplierAndBatch() throws Exception {
        // given
        fruitService.create(FruitCreateRequest.builder()
                .name("Maracuya")
                .organic(false)
                .batchNumber("C997562")
                .supplier("EcuadorTropical")
                .build()
        );
        fruitService.create(FruitCreateRequest.builder()
                .name("Dragon Fruit")
                .batchNumber("C997562")
                .supplier("EcuadorTropical")
                .build()
        );
        fruitService.create(FruitCreateRequest.builder()
                .name("Mango")
                .batchNumber("BN85721")
                .supplier("EcuadorTropical")
                .build()
        );

        // when & then
        mockMvc.perform(get(BASE_URL + "/supplier/EcuadorTropical/batch/C997562"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].supplier").value("EcuadorTropical"))
                .andExpect(jsonPath("$[0].batchNumber").value("C997562"))
                .andExpect(jsonPath("$[1].supplier").value("EcuadorTropical"))
                .andExpect(jsonPath("$[1].batchNumber").value("C997562"));
    }

    static Stream<FruitCreateRequest> invalidCreateRequests() {
        return Stream.of(
                FruitCreateRequest.builder().name(null).supplier(FruitFixture.SUPPLIER_A).batchNumber(FruitFixture.BATCH_NUMBER).build(),
                FruitCreateRequest.builder().name("").supplier(FruitFixture.SUPPLIER_A).batchNumber(FruitFixture.BATCH_NUMBER).build(),
                FruitCreateRequest.builder().name(FruitFixture.FRUIT_NAME_APPLE).supplier(null).batchNumber(FruitFixture.BATCH_NUMBER).build(),
                FruitCreateRequest.builder().name(FruitFixture.FRUIT_NAME_APPLE).supplier("").batchNumber(FruitFixture.BATCH_NUMBER).build(),
                FruitCreateRequest.builder().name(FruitFixture.FRUIT_NAME_APPLE).supplier(FruitFixture.SUPPLIER_A).batchNumber(null).build(),
                FruitCreateRequest.builder().name(FruitFixture.FRUIT_NAME_APPLE).supplier(FruitFixture.SUPPLIER_A).batchNumber("").build()
        );
    }
}
