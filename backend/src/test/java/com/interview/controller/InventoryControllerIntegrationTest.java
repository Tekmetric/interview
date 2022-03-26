package com.interview.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class InventoryControllerIntegrationTest {

    private static final String GET_ALL_INVENTORIES_URL = "/api/inventories";

    @Autowired
    private MockMvc restOperationMockMvc;

    @Test
    @Transactional
    void test_getAllInventories() throws Exception {

        restOperationMockMvc.perform(
                get(GET_ALL_INVENTORIES_URL)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
