package com.interview.mapper;

import com.interview.dto.ProductCreateRequest;
import com.interview.dto.ProductResponse;
import com.interview.dto.ProductUpdateRequest;
import com.interview.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Product Mapper Tests")
class ProductMapperTest {

    private ProductMapper productMapper;
    private Product testProduct;
    private ProductCreateRequest testCreateRequest;
    private ProductUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);

        testProduct = new Product("TEST-001", "Test Product", "Electronics", "each", new BigDecimal("99.99"));
        testProduct.setId(1L);
        testProduct.setActive(true);
        testProduct.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        testProduct.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 15, 30));

        testCreateRequest = new ProductCreateRequest(
                "NEW-001", "New Product", "New product description", 
                "Office Supplies", "piece", new BigDecimal("149.99"));

        testUpdateRequest = new ProductUpdateRequest(
                "UPDATED-001", "Updated Product", "Updated description", 
                "Furniture", "item", new BigDecimal("199.99"), false);
    }

    @Test
    @DisplayName("Should map ProductCreateRequest to Product entity")
    void shouldMapProductCreateRequestToEntity() {
        // When
        Product result = productMapper.toEntity(testCreateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("NEW-001");
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getCategory()).isEqualTo("Office Supplies");
        assertThat(result.getUnit()).isEqualTo("piece");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("149.99"));
        assertThat(result.getId()).isNull(); // Should not be mapped
        assertThat(result.getActive()).isTrue(); // Should use default value
    }

    @Test
    @DisplayName("Should map ProductUpdateRequest to Product entity")
    void shouldMapProductUpdateRequestToEntity() {
        // When
        Product result = productMapper.toEntity(testUpdateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("UPDATED-001");
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getCategory()).isEqualTo("Furniture");
        assertThat(result.getUnit()).isEqualTo("item");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(result.getActive()).isEqualTo(false);
        assertThat(result.getId()).isNull(); // Should not be mapped
    }

    @Test
    @DisplayName("Should map Product entity to ProductResponse")
    void shouldMapProductEntityToResponse() {
        // When
        ProductResponse result = productMapper.toResponse(testProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.sku()).isEqualTo("TEST-001");
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.category()).isEqualTo("Electronics");
        assertThat(result.unit()).isEqualTo("each");
        assertThat(result.price()).isEqualTo(new BigDecimal("99.99"));
        assertThat(result.active()).isEqualTo(true);
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 2, 15, 30));
    }

    @Test
    @DisplayName("Should map list of Product entities to list of ProductResponses")
    void shouldMapProductListToResponseList() {
        // Given
        Product product2 = new Product("TEST-002", "Test Product 2", "Electronics", "each", new BigDecimal("199.99"));
        product2.setId(2L);
        List<Product> productList = List.of(testProduct, product2);

        // When
        List<ProductResponse> result = productMapper.toResponseList(productList);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        ProductResponse response1 = result.get(0);
        assertThat(response1.id()).isEqualTo(1L);
        assertThat(response1.sku()).isEqualTo("TEST-001");
        assertThat(response1.name()).isEqualTo("Test Product");
        
        ProductResponse response2 = result.get(1);
        assertThat(response2.id()).isEqualTo(2L);
        assertThat(response2.sku()).isEqualTo("TEST-002");
        assertThat(response2.name()).isEqualTo("Test Product 2");
    }

    @Test
    @DisplayName("Should update existing Product entity from ProductUpdateRequest")
    void shouldUpdateExistingEntityFromRequest() {
        // Given
        Product existingProduct = new Product("OLD-001", "Old Product", "Books", "each", new BigDecimal("49.99"));
        existingProduct.setId(5L);
        existingProduct.setActive(true);

        // When
        productMapper.updateEntityFromRequest(testUpdateRequest, existingProduct);

        // Then
        assertThat(existingProduct.getId()).isEqualTo(5L); // Should preserve ID
        assertThat(existingProduct.getSku()).isEqualTo("UPDATED-001");
        assertThat(existingProduct.getName()).isEqualTo("Updated Product");
        assertThat(existingProduct.getCategory()).isEqualTo("Furniture");
        assertThat(existingProduct.getUnit()).isEqualTo("item");
        assertThat(existingProduct.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(existingProduct.getActive()).isEqualTo(false);
    }

    @Test
    @DisplayName("Should handle null values appropriately in mapping")
    void shouldHandleNullValuesInMapping() {
        // Given
        ProductCreateRequest nullRequest = new ProductCreateRequest(
                "NULL-001", null, null, null, null, null);

        // When
        Product result = productMapper.toEntity(nullRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("NULL-001");
        assertThat(result.getName()).isNull();
        assertThat(result.getCategory()).isNull();
        assertThat(result.getUnit()).isNull();
        assertThat(result.getPrice()).isNull();
    }

    @Test
    @DisplayName("Should handle empty list mapping")
    void shouldHandleEmptyListMapping() {
        // Given
        List<Product> emptyList = List.of();

        // When
        List<ProductResponse> result = productMapper.toResponseList(emptyList);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle null entity in response mapping")
    void shouldHandleNullEntityInResponseMapping() {
        // When
        ProductResponse result = productMapper.toResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should preserve precision for price values")
    void shouldPreservePrecisionForPriceValues() {
        // Given
        ProductCreateRequest precisionRequest = new ProductCreateRequest(
                "PRECISION-001", "Precision Test", "Test description", 
                "Test", "each", new BigDecimal("123.456"));

        // When
        Product result = productMapper.toEntity(precisionRequest);

        // Then
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("123.456"));
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        // Given
        ProductCreateRequest specialRequest = new ProductCreateRequest(
                "SPECIAL-001", "Product with Special Chars: àáâãäåæç & <script>", 
                "Special description", "Category/Subcategory", "each", new BigDecimal("99.99"));

        // When
        Product result = productMapper.toEntity(specialRequest);

        // Then
        assertThat(result.getName()).isEqualTo("Product with Special Chars: àáâãäåæç & <script>");
        assertThat(result.getCategory()).isEqualTo("Category/Subcategory");
    }
}