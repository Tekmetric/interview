package com.interview.service;

import com.interview.entity.Product;
import com.interview.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findAllActiveCategories();
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String search, String category, Boolean active) {
        return productRepository.searchProducts(search, category, active);
    }
    
    public Product createProduct(Product product) {
        // Validate SKU uniqueness
        if (productRepository.findBySku(product.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU '" + product.getSku() + "' already exists");
        }
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Check SKU uniqueness if it's being changed
        if (!existingProduct.getSku().equals(updatedProduct.getSku())) {
            productRepository.findBySku(updatedProduct.getSku()).ifPresent(p -> {
                throw new IllegalArgumentException("Product with SKU '" + updatedProduct.getSku() + "' already exists");
            });
        }
        
        // Update fields
        existingProduct.setSku(updatedProduct.getSku());
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setUnit(updatedProduct.getUnit());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setActive(updatedProduct.getActive());
        
        return productRepository.save(existingProduct);
    }
    
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}