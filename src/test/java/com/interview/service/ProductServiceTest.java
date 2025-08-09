package com.interview.service;

import com.interview.entity.Product;
import com.interview.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("TEST-001", "Test Product", "Electronics", "each", BigDecimal.valueOf(99.99));
        testProduct.setId(1L);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        existingProduct = new Product("EXIST-001", "Existing Product", "Electronics", "each",  BigDecimal.valueOf(199.99));
        existingProduct.setId(2L);
        existingProduct.setCreatedAt(LocalDateTime.now());
        existingProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get all active products")
    void shouldGetAllActiveProducts() {
        // Given
        List<Product> activeProducts = List.of(testProduct, existingProduct);
        when(productRepository.findByActiveTrue()).thenReturn(activeProducts);

        // When
        List<Product> result = productService.getAllActiveProducts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testProduct, existingProduct);
        verify(productRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should get all products with pagination")
    void shouldGetAllProductsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(testProduct);
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should find product by ID when exists")
    void shouldFindProductByIdWhenExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.getProductById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void shouldReturnEmptyWhenProductNotFoundById() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProductById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        when(productRepository.findBySku("TEST-001")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.createProduct(testProduct);

        // Then
        assertThat(result).isEqualTo(testProduct);
        verify(productRepository).findBySku("TEST-001");
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when creating product with duplicate SKU")
    void shouldThrowExceptionWhenCreatingProductWithDuplicateSku() {
        // Given
        when(productRepository.findBySku("TEST-001")).thenReturn(Optional.of(existingProduct));

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(testProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with SKU 'TEST-001' already exists");

        verify(productRepository).findBySku("TEST-001");
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        Product updatedProduct = new Product("TEST-002", "Updated Product", "Furniture", "each", BigDecimal.valueOf(299.99));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySku("TEST-002")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.updateProduct(1L, updatedProduct);

        // Then
        assertThat(result).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
        verify(productRepository).findBySku("TEST-002");
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(999L, testProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should deactivate product successfully")
    void shouldDeactivateProductSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.deactivateProduct(1L);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
        assertThat(testProduct.getActive()).isFalse();
    }

    @Test
    @DisplayName("Should search products with filters")
    void shouldSearchProductsWithFilters() {
        // Given
        List<Product> searchResults = List.of(testProduct);
        when(productRepository.searchProducts("test", "Electronics", true))
                .thenReturn(searchResults);

        // When
        List<Product> result = productService.searchProducts("test", "Electronics", true);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testProduct);
        verify(productRepository).searchProducts("test", "Electronics", true);
    }

    @Test
    @DisplayName("Should get all categories")
    void shouldGetAllCategories() {
        // Given
        List<String> categories = List.of("Electronics", "Furniture", "Office Supplies");
        when(productRepository.findAllActiveCategories()).thenReturn(categories);

        // When
        List<String> result = productService.getAllCategories();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Electronics", "Furniture", "Office Supplies");
        verify(productRepository).findAllActiveCategories();
    }

    @Test
    @DisplayName("Should delete product when exists")
    void shouldDeleteProductWhenExists() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).existsById(999L);
        verify(productRepository, never()).deleteById(any());
    }
}