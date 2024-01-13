package com.interview.products;

import com.interview.products.api.BulkProductResponse;
import com.interview.products.api.CreateProductParams;
import com.interview.products.api.ProductResponse;
import com.interview.products.api.ReserveProductParams;
import com.interview.products.data.Product;
import com.interview.products.data.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core logic for product creation and updates
 */
@Component
public class ProductController {
    private static final int MAX_PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a new product
     * @param createProductParams params for creating a new product
     * @return the created product
     */
    @Transactional
    public ProductResponse createProduct(CreateProductParams createProductParams) {
        Product product = new Product(UUID.randomUUID(), createProductParams.getName(),
                createProductParams.getCurrency(), createProductParams.getPrice(),
                createProductParams.getQuantity().orElse(null));
        productRepository.save(product);
        LOG.info("Successfully saved new product {} with name {}", product.getId(), product.getName());
        return convertToResponse(product);
    }

    /**
     * Reserve a specific quantity of a given product
     * @param reserveProductParams params specifying the product id and quantity to reserve
     * @return the updated product reflecting the remaining available quantity
     */
    @Transactional
    public ProductResponse reserveProduct(ReserveProductParams reserveProductParams) {
        Product product = productRepository.findAndLockById(reserveProductParams.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(reserveProductParams.getProductId()));
        if (product.getQuantity().isEmpty()) {
            LOG.error("Cannot reserve quantity for service product {}", product.getId());
            throw new IllegalArgumentException("Cannot reserve quantity on a service product");
        }
        int oldQuantity = product.getQuantity().get();
        if (oldQuantity <= reserveProductParams.getQuantity()) {
            LOG.error("Cannot reserve quantity {} for product {}. Available quantity is {}",
                    reserveProductParams.getQuantity(), product.getId(), oldQuantity);
            throw new IllegalArgumentException("Product quantity " + reserveProductParams.getQuantity() + " not available");
        }
        product.setQuantity(oldQuantity - reserveProductParams.getQuantity());
        product = productRepository.save(product);
        LOG.info("Successfully updated quantity for product {} from {} to {}", product.getId(),
                oldQuantity, product.getQuantity());
        return convertToResponse(product);
    }

    /**
     * Get an existing product
     * @param productId the id of the product to get
     * @return the product
     */
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        Product product = findByIdNotNull(productId);
        return convertToResponse(product);
    }

    /**
     * Get a page of products, sorted by id. This method uses this maximum id from the last page to support stable
     * pagination (to account for products added or removed between API calls)
     * @param idGreaterThan the maximum id from the last page, if applicable
     * @param limit the desired page size (100 max)
     * @return a single page of products
     */
    @Transactional(readOnly = true)
    public BulkProductResponse getPageOfProducts(@Nullable UUID idGreaterThan, int limit) {
        if (limit > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Limit cannot be greater than " + MAX_PAGE_SIZE);
        }
        Slice<Product> products;
        if (idGreaterThan == null) {
            PageRequest page = PageRequest.of(0, limit, Sort.by("id"));
            products = productRepository.findAll(page);
        } else {
            PageRequest page = PageRequest.of(0, limit);
            products = productRepository.getByIdGreaterThanOrderById(idGreaterThan, page);
        }
        List<ProductResponse> responseList = products.stream().map(this::convertToResponse)
            .collect(Collectors.toList());
        return new BulkProductResponse(responseList, products.hasNext());

    }

    /**
     * Delete an existing product
     * @param productId the id of the product to delete
     */
    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = findByIdNotNull(productId);
        productRepository.delete(product);
        LOG.info("Successfully deleted product {}", product.getId());
    }

    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getCurrency(),
                product.getPrice(), product.getQuantity().orElse(null));
    }

    private Product findByIdNotNull(UUID id) {
        Optional<Product> maybeProduct = productRepository.findById(id);
        if (maybeProduct.isPresent()) {
            return maybeProduct.get();
        }
        LOG.error("Product {} does not exist", id);
        throw new ProductNotFoundException(id);
    }
}
