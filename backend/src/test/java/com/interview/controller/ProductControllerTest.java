package com.interview.controller;

import com.interview.domain.Comment;
import com.interview.domain.Product;
import com.interview.exceptions.ProductNotFoundException;
import com.interview.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void testFindById() throws Exception {
        Product product = new Product();
        product.setName("name");

        when(productService.findProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductById"))
                .andExpect(jsonPath("$.name", is(product.getName())));

    }

    @Test
    public void testFindById_NotFound() throws Exception {
        when(productService.findProductById(1L)).thenThrow(new ProductNotFoundException(1L));
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testDeleteProduct() throws Exception {
        Product product = new Product();
        product.setName("name");

        when(productService.findProductById(1L)).thenReturn(product);

        mockMvc.perform(delete("/products/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("deleteProduct"))
                .andExpect(content().string("1"));

    }

    @Test
    public void testGetProductsPaginatedSortedById() throws Exception {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        List<Product> products = setupProducts();
        when(productService.findProductsPaginated(pageable)).thenReturn(products);

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("pageSize", "2")
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsPaginatedSortedById"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("p1")));


    }
    @Test
    public void testGetProductsPaginatedSortedById_NoResults() throws Exception {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        when(productService.findProductsPaginated(pageable)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("pageSize", "2")
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsPaginatedSortedById"))
                .andExpect(jsonPath("$", hasSize(0)));
    }



    private List<Product> setupProducts() {
        Product p1 = Product.builder()
                .id(1L)
                .name("p1")
                .comments(List.of(
                        Comment.builder().id(1L).build()
                ))
                .build();
        Product p2 = Product.builder()
                .id(2L).name("p2")
                .comments(List.of(
                        Comment.builder().id(2L).build()
                ))
                .build();
        return Arrays.asList(p1, p2);
    }
}