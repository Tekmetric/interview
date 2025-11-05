package com.interview;

import com.interview.model.Ingredient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IngredientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCRUDWorkflow_ShouldWorkEndToEnd() throws Exception {
        // 1. CREATE - Add new ingredient
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName("Integration Test Tomatoes");
        newIngredient.setCategory("Vegetables");
        newIngredient.setQuantity(100.0);
        newIngredient.setUnit("kg");
        newIngredient.setMinimumStock(20.0);
        newIngredient.setPricePerUnit(new BigDecimal("3.75"));
        newIngredient.setSupplier("Test Supplier");
        newIngredient.setExpirationDate(LocalDate.of(2025, 12, 31));

        MvcResult createResult = mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Integration Test Tomatoes")))
                .andReturn();

        // Extract the created ID
        String responseBody = createResult.getResponse().getContentAsString();
        Ingredient createdIngredient = objectMapper.readValue(responseBody, Ingredient.class);
        Long createdId = createdIngredient.getId();

        // 2. READ - Get the created ingredient
        mockMvc.perform(get("/api/ingredients/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdId.intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Tomatoes")))
                .andExpect(jsonPath("$.quantity", is(100.0)));

        // 3. UPDATE - Modify the ingredient
        newIngredient.setQuantity(80.0);
        newIngredient.setPricePerUnit(new BigDecimal("4.00"));

        mockMvc.perform(put("/api/ingredients/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(80.0)))
                .andExpect(jsonPath("$.pricePerUnit", is(4.00)));

        // 4. GET ALL - Verify it's in the list
        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[?(@.id == " + createdId + ")]").exists());

        // 5. SEARCH - Find by name
        mockMvc.perform(get("/api/ingredients/search")
                        .param("name", "Integration Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name", containsString("Integration Test")));

        // 6. DELETE - Remove the ingredient
        mockMvc.perform(delete("/api/ingredients/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Ingredient deleted successfully")));

        // 7. VERIFY DELETION - Should return 404
        mockMvc.perform(get("/api/ingredients/" + createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void filteringAndSearching_ShouldWorkCorrectly() throws Exception {
        // Create test data
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Premium Olive Oil");
        ingredient1.setCategory("Oils");
        ingredient1.setQuantity(50.0);
        ingredient1.setUnit("liters");
        ingredient1.setMinimumStock(10.0);
        ingredient1.setPricePerUnit(new BigDecimal("12.00"));
        ingredient1.setSupplier("Mediterranean Imports");
        ingredient1.setExpirationDate(LocalDate.of(2025, 10, 31));

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Extra Virgin Olive Oil");
        ingredient2.setCategory("Oils");
        ingredient2.setQuantity(30.0);
        ingredient2.setUnit("liters");
        ingredient2.setMinimumStock(10.0);
        ingredient2.setPricePerUnit(new BigDecimal("15.00"));
        ingredient2.setSupplier("Mediterranean Imports");
        ingredient2.setExpirationDate(LocalDate.of(2025, 11, 30));

        mockMvc.perform(post("/api/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ingredient2)))
                .andExpect(status().isCreated());

        // Test category filtering
        mockMvc.perform(get("/api/ingredients/category/Oils"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));

        // Test supplier filtering
        mockMvc.perform(get("/api/ingredients/supplier/Mediterranean Imports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));

        // Test search
        mockMvc.perform(get("/api/ingredients/search")
                        .param("name", "olive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void lowStockDetection_ShouldIdentifyLowStockItems() throws Exception {
        // Create a low stock item
        Ingredient lowStockItem = new Ingredient();
        lowStockItem.setName("Low Stock Basil");
        lowStockItem.setCategory("Herbs");
        lowStockItem.setQuantity(0.5);
        lowStockItem.setUnit("kg");
        lowStockItem.setMinimumStock(2.0);
        lowStockItem.setPricePerUnit(new BigDecimal("18.00"));
        lowStockItem.setSupplier("Herb Garden");
        lowStockItem.setExpirationDate(LocalDate.of(2024, 11, 10));

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowStockItem)))
                .andExpect(status().isCreated());

        // Check low stock endpoint
        mockMvc.perform(get("/api/ingredients/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Low Stock Basil')]").exists());
    }

    @Test
    void errorHandling_ShouldReturnProperErrorResponses() throws Exception {
        // Test 404 for non-existent ID
        mockMvc.perform(get("/api/ingredients/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());

        // Test 404 for delete non-existent
        mockMvc.perform(delete("/api/ingredients/99999"))
                .andExpect(status().isNotFound());
    }
}
