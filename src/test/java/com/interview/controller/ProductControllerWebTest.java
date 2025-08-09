package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.ProductCreateRequest;
import com.interview.dto.ProductResponse;
import com.interview.dto.ProductUpdateRequest;
import com.interview.entity.Product;
import com.interview.mapper.ProductMapper;
import com.interview.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ProductController.class)
@WithMockUser
class ProductControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_shouldReturnPagedProducts() throws Exception {
        // Given
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        Page<Product> productPage = new PageImpl<>(List.of(product));
        Page<ProductResponse> responsePage = new PageImpl<>(List.of(response));
        
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.content[0].sku").value("TEST-001"));
    }

    @Test
    void getAllActiveProducts_shouldReturnActiveProducts() throws Exception {
        // Given
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productService.getAllActiveProducts()).thenReturn(List.of(product));
        when(productMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void getProductById_shouldReturnProduct_whenExists() throws Exception {
        // Given
        Long productId = 1L;
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productService.getProductById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductById_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        Long productId = 999L;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductBySku_shouldReturnProduct_whenExists() throws Exception {
        // Given
        String sku = "TEST-001";
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productService.getProductBySku(sku)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/products/sku/{sku}", sku))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sku").value("TEST-001"));
    }

    @Test
    void getProductBySku_shouldReturnNotFound_whenNotExists() throws Exception {
        // Given
        String sku = "NON-EXISTENT";
        when(productService.getProductBySku(sku)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/sku/{sku}", sku))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_shouldReturnCategories() throws Exception {
        // Given
        List<String> categories = List.of("Electronics", "Books", "Clothing");
        when(productService.getAllCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Electronics"))
                .andExpect(jsonPath("$[1]").value("Books"))
                .andExpect(jsonPath("$[2]").value("Clothing"));
    }

    @Test
    void searchProducts_shouldReturnFilteredProducts() throws Exception {
        // Given
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productService.searchProducts("test", "Electronics", true))
                .thenReturn(List.of(product));
        when(productMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/products/search")
                        .param("search", "test")
                        .param("category", "Electronics")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void createProduct_shouldCreateProduct() throws Exception {
        // Given
        ProductCreateRequest request = new ProductCreateRequest(
                "NEW-001", "New Product", "Test Description", "Electronics", "piece", new BigDecimal("99.99")
        );
        
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productMapper.toEntity(any(ProductCreateRequest.class))).thenReturn(product);
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void createProduct_shouldReturnBadRequest_whenInvalidData() throws Exception {
        // Given
        ProductCreateRequest invalidRequest = new ProductCreateRequest(
                "", "", "", "", "", null  // Invalid data
        );

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_shouldUpdateProduct() throws Exception {
        // Given
        Long productId = 1L;
        ProductUpdateRequest request = new ProductUpdateRequest(
                "UPDATED-001", "Updated Product", "Updated Description", "Electronics", "piece", new BigDecimal("199.99"), true
        );
        
        Product product = createTestProduct();
        ProductResponse response = createTestProductResponse();
        
        when(productMapper.toEntity(any(ProductUpdateRequest.class))).thenReturn(product);
        when(productService.updateProduct(eq(productId), any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteProduct_shouldDeleteProduct() throws Exception {
        // Given
        Long productId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateProduct_shouldDeactivateProduct() throws Exception {
        // Given
        Long productId = 1L;

        // When & Then
        mockMvc.perform(patch("/api/products/{id}/deactivate", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("TEST-001");
        product.setName("Test Product");
        product.setCategory("Electronics");
        product.setDescription("Test Description");
        product.setUnit("piece");
        product.setPrice(new BigDecimal("99.99"));
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private ProductResponse createTestProductResponse() {
        return new ProductResponse(
                1L, "TEST-001", "Test Product", "Test Description", "Electronics", "piece",
                new BigDecimal("99.99"), true, LocalDateTime.now(), LocalDateTime.now()
        );
    }
}