package com.interview.repository;

import com.interview.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") 
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Product inactiveProduct;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product("TEST-001", "Test Product 1", "Electronics", "each", new BigDecimal("99.99"));
        testProduct2 = new Product("TEST-002", "Test Product 2", "Electronics", "each", new BigDecimal("199.99"));
        inactiveProduct = new Product("INACTIVE-001", "Inactive Product", "Furniture", "each", new BigDecimal("299.99"));
        inactiveProduct.setActive(false);

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);
        entityManager.persistAndFlush(inactiveProduct);
    }

    @Test
    @DisplayName("Should find product by SKU")
    void shouldFindProductBySku() {
        // When
        Optional<Product> result = productRepository.findBySku("TEST-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Product 1");
        assertThat(result.get().getSku()).isEqualTo("TEST-001");
    }

    @Test
    @DisplayName("Should return empty when SKU not found")
    void shouldReturnEmptyWhenSkuNotFound() {
        // When
        Optional<Product> result = productRepository.findBySku("NON-EXISTENT");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find only active products")
    void shouldFindOnlyActiveProducts() {
        // When
        List<Product> result = productRepository.findByActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getSku)
                .containsExactlyInAnyOrder("TEST-001", "TEST-002");
        assertThat(result).allMatch(Product::getActive);
    }

    @Test
    @DisplayName("Should search products by name")
    void shouldSearchProductsByName() {
        // When
        List<Product> result = productRepository.searchProducts("Test Product 1", null, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product 1");
    }

    @Test
    @DisplayName("Should search products by category")
    void shouldSearchProductsByCategory() {
        // When
        List<Product> result = productRepository.searchProducts(null, "Electronics", null);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getCategory)
                .containsOnly("Electronics");
    }

    @Test
    @DisplayName("Should search products by active status")
    void shouldSearchProductsByActiveStatus() {
        // When
        List<Product> result = productRepository.searchProducts(null, null, true);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Product::getActive);
    }

    @Test
    @DisplayName("Should search products with all filters")
    void shouldSearchProductsWithAllFilters() {
        // When
        List<Product> result = productRepository.searchProducts("Test", "Electronics", true);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getCategory)
                .containsOnly("Electronics");
        assertThat(result).allMatch(Product::getActive);
        assertThat(result).allMatch(p -> p.getName().contains("Test"));
    }

    @Test
    @DisplayName("Should find all active categories")
    void shouldFindAllActiveCategories() {
        // When
        List<String> result = productRepository.findAllActiveCategories();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("Electronics");
    }

    @Test
    @DisplayName("Should handle case-insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        // When
        List<Product> result = productRepository.searchProducts("test product", null, null);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product 1", "Test Product 2");
    }

    @Test
    @DisplayName("Should return empty list when no products match search")
    void shouldReturnEmptyListWhenNoProductsMatchSearch() {
        // When
        List<Product> result = productRepository.searchProducts("NonExistent", null, null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find products with partial name match")
    void shouldFindProductsWithPartialNameMatch() {
        // When
        List<Product> result = productRepository.searchProducts("Product 1", null, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product 1");
    }
}