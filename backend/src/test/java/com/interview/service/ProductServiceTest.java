package com.interview.service;


import com.interview.domain.Product;
import com.interview.exceptions.ProductNotFoundException;
import com.interview.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static com.interview.exceptions.ProductNotFoundException.COULD_NOT_FIND_PRODUCT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;


    @Test
    public void testFindById_Found() {
        Product product = new Product();
        product.setName("name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findProductById(1L);
        assertEquals(product.getName(), result.getName());
    }

    @Test
    public void testFindById_NotFound() {
        Product product = new Product();
        product.setName("name");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductNotFoundException result = assertThrows(ProductNotFoundException.class, () -> {
            productService.findProductById(1L);
        });

        assertEquals(COULD_NOT_FIND_PRODUCT_ID + "1", result.getMessage());

    }

}