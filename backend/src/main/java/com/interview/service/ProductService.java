package com.interview.service;

import com.interview.dao.ProductRepository;
import com.interview.dao.model.Product;
import com.interview.dao.model.QProduct;
import com.interview.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get a product by ID. If not found, a {@link ProductNotFoundException} is thrown.
     * Soft-deleted products are still returned - see {@link Product#getDeletedDate()}.
     *
     * @param id The ID of the product.
     */
    public Product getById(final long id) throws ProductNotFoundException {
        return findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }

    /**
     * Find a product by ID. If not found, {@link Optional#empty()} is returned.
     *
     * @param id The ID of the product.
     */
    public Optional<Product> findById(final long id) {
        return productRepository.findById(id);
    }

    /**
     * Get a page of products based on the pagination requested.
     *
     * @param pageable       The pagination request.
     * @param includeDeleted If true, soft-deleted products are also returned.
     */
    public Page<Product> getPage(final Pageable pageable, boolean includeDeleted) {
        if (includeDeleted) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findAll(QProduct.product.deletedDate.isNull(), pageable);
        }
    }

    /**
     * Create a product.
     *
     * @param product The product to create.
     */
    public Product create(final Product product) {
        checkArgument(product.getId() == null, "Product may already exist");
        log.debug("Creating new product {}", product);
        return productRepository.save(product);
    }

    /**
     * Update a product.
     *
     * @param product The product to update.
     */
    @Transactional
    public Product update(final Product product) {
        checkArgument(product.getId() != null, "ID is required");

        final Product updated = getById(product.getId());
        updated.setName(product.getName());
        updated.setDescription(product.getDescription());
        updated.setPrice(product.getPrice());
        updated.setVersion(product.getVersion());

        log.debug("Updating product {}", updated);
        return productRepository.save(updated);
    }

    /**
     * Soft-delete a product.
     *
     * @param id The product ID.
     */
    @Transactional
    public void deleteById(final long id) {
        final Product product = getById(id);
        if (product.getDeletedDate() == null) {
            log.debug("Deleting product {}", product);
            product.setDeletedDate(Instant.now());
            productRepository.save(product);
        }
    }
}
