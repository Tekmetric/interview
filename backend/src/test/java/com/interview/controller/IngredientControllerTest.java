package com.interview.controller;

import com.interview.model.Ingredient;
import com.interview.service.IngredientService;
import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngredientService ingredientService;

    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testIngredient = new Ingredient();
        testIngredient.setId(1L);
        testIngredient.setName("Fresh Tomatoes");
        testIngredient.setCategory("Vegetables");
        testIngredient.setQuantity(25.0);
        testIngredient.setUnit("kg");
        testIngredient.setMinimumStock(10.0);
        testIngredient.setPricePerUnit(new BigDecimal("3.50"));
        testIngredient.setSupplier("Local Farm");
        testIngredient.setExpirationDate(LocalDate.of(2025, 12, 31));
    }

    @Test
    void getAllIngredients_ShouldReturnList() throws Exception {
        // Arrange
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        ingredient2.setName("Olive Oil");
        ingredient2.setCategory("Oils");
        ingredient2.setQuantity(50.0);
        ingredient2.setUnit("liters");
        ingredient2.setMinimumStock(10.0);
        ingredient2.setPricePerUnit(new BigDecimal("8.50"));

        List<Ingredient> ingredients = Arrays.asList(testIngredient, ingredient2);
        when(ingredientService.getAllIngredients()).thenReturn(ingredients);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Fresh Tomatoes")))
                .andExpect(jsonPath("$[1].name", is("Olive Oil")));

        verify(ingredientService, times(1)).getAllIngredients();
    }

    @Test
    void getIngredientById_WhenExists_ShouldReturnIngredient() throws Exception {
        // Arrange
        when(ingredientService.getIngredientById(1L)).thenReturn(testIngredient);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Fresh Tomatoes")))
                .andExpect(jsonPath("$.category", is("Vegetables")))
                .andExpect(jsonPath("$.quantity", is(25.0)));

        verify(ingredientService, times(1)).getIngredientById(1L);
    }

    @Test
    void getIngredientById_WhenNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(ingredientService.getIngredientById(999L))
                .thenThrow(new EntityNotFoundException("Ingredient not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Ingredient not found")));

        verify(ingredientService, times(1)).getIngredientById(999L);
    }

    @Test
    void createIngredient_ShouldReturnCreatedIngredient() throws Exception {
        // Arrange
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName("Garlic");
        newIngredient.setCategory("Vegetables");
        newIngredient.setQuantity(8.0);
        newIngredient.setUnit("kg");
        newIngredient.setMinimumStock(3.0);
        newIngredient.setPricePerUnit(new BigDecimal("4.25"));
        newIngredient.setSupplier("Local Farm");
        newIngredient.setExpirationDate(LocalDate.of(2025, 11, 15));

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setId(3L);
        savedIngredient.setName(newIngredient.getName());
        savedIngredient.setCategory(newIngredient.getCategory());
        savedIngredient.setQuantity(newIngredient.getQuantity());
        savedIngredient.setUnit(newIngredient.getUnit());
        savedIngredient.setMinimumStock(newIngredient.getMinimumStock());
        savedIngredient.setPricePerUnit(newIngredient.getPricePerUnit());
        savedIngredient.setSupplier(newIngredient.getSupplier());
        savedIngredient.setExpirationDate(newIngredient.getExpirationDate());

        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(savedIngredient);

        // Act & Assert
        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Garlic")))
                .andExpect(jsonPath("$.category", is("Vegetables")));

        verify(ingredientService, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    void updateIngredient_ShouldReturnUpdatedIngredient() throws Exception {
        // Arrange
        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setId(1L);
        updatedIngredient.setName("Updated Tomatoes");
        updatedIngredient.setCategory("Vegetables");
        updatedIngredient.setQuantity(30.0);
        updatedIngredient.setUnit("kg");
        updatedIngredient.setMinimumStock(10.0);
        updatedIngredient.setPricePerUnit(new BigDecimal("4.00"));
        updatedIngredient.setSupplier("New Farm");
        updatedIngredient.setExpirationDate(LocalDate.of(2025, 12, 31));

        when(ingredientService.updateIngredient(eq(1L), any(Ingredient.class)))
                .thenReturn(updatedIngredient);

        // Act & Assert
        mockMvc.perform(put("/api/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedIngredient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Tomatoes")))
                .andExpect(jsonPath("$.quantity", is(30.0)))
                .andExpect(jsonPath("$.pricePerUnit", is(4.00)));

        verify(ingredientService, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    void deleteIngredient_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(ingredientService).deleteIngredient(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Ingredient deleted successfully")))
                .andExpect(jsonPath("$.id", is("1")));

        verify(ingredientService, times(1)).deleteIngredient(1L);
    }

    @Test
    void getIngredientsByCategory_ShouldReturnFilteredList() throws Exception {
        // Arrange
        List<Ingredient> vegetables = Arrays.asList(testIngredient);
        when(ingredientService.getIngredientsByCategory("Vegetables")).thenReturn(vegetables);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/category/Vegetables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Vegetables")));

        verify(ingredientService, times(1)).getIngredientsByCategory("Vegetables");
    }

    @Test
    void getIngredientsBySupplier_ShouldReturnFilteredList() throws Exception {
        // Arrange
        List<Ingredient> farmItems = Arrays.asList(testIngredient);
        when(ingredientService.getIngredientsBySupplier("Local Farm")).thenReturn(farmItems);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/supplier/Local Farm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].supplier", is("Local Farm")));

        verify(ingredientService, times(1)).getIngredientsBySupplier("Local Farm");
    }

    @Test
    void searchIngredients_ShouldReturnMatchingItems() throws Exception {
        // Arrange
        List<Ingredient> searchResults = Arrays.asList(testIngredient);
        when(ingredientService.searchIngredientsByName("tomato")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/search")
                        .param("name", "tomato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", containsString("Tomato")));

        verify(ingredientService, times(1)).searchIngredientsByName("tomato");
    }

    @Test
    void getLowStockIngredients_ShouldReturnLowStockItems() throws Exception {
        // Arrange
        Ingredient lowStockItem = new Ingredient();
        lowStockItem.setId(2L);
        lowStockItem.setName("Basil");
        lowStockItem.setCategory("Herbs");
        lowStockItem.setQuantity(0.5);
        lowStockItem.setUnit("kg");
        lowStockItem.setMinimumStock(1.0);
        lowStockItem.setPricePerUnit(new BigDecimal("15.00"));

        List<Ingredient> lowStockItems = Arrays.asList(lowStockItem);
        when(ingredientService.getLowStockIngredients()).thenReturn(lowStockItems);

        // Act & Assert
        mockMvc.perform(get("/api/ingredients/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Basil")));

        verify(ingredientService, times(1)).getLowStockIngredients();
    }
}
