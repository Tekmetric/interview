package com.interview.service;


import com.interview.controller.dto.ProductDto;
import com.interview.domain.Comment;
import com.interview.domain.Product;
import com.interview.exceptions.ProductNotFoundException;
import com.interview.repository.ProductRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id).
                orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findProductsPaginated(Pageable pageable) {
        return productRepository.findAll(pageable).toList();
    }

    @Transactional
    public void addCommentToPost(Long productId, Comment comment) {
        Product product = findProductById(productId);
        product.addComment(comment);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ProductNotFoundException(id);
        }
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            return updateProductAttributes(productDto, productOptional.get());
        }

        throw new ProductNotFoundException(id);
    }

    public long getProductsTotalCount() {
        return productRepository.count();
    }

    private Product updateProductAttributes(ProductDto productDto, Product product) {
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setName(productDto.getName());

        return product;
    }
}
