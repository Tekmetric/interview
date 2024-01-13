package com.interview.resource;

import com.interview.products.ProductController;
import com.interview.products.ProductNotFoundException;
import com.interview.products.api.BulkProductResponse;
import com.interview.products.api.CreateProductParams;
import com.interview.products.api.ProductResponse;
import com.interview.products.api.ReserveProductParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductResource {

    private final ProductController productController;

    @Autowired
    public ProductResource(ProductController productController) {
        this.productController = productController;
    }

    @PostMapping("/create")
    @ResponseBody
    public ProductResponse createProduct(@RequestBody @Valid CreateProductParams createProductParams) {
        return productController.createProduct(createProductParams);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ProductResponse getProduct(@PathVariable("id") @NotNull UUID id) {
        return productController.getProduct(id);
    }

    @GetMapping("/bulk")
    @ResponseBody
    public BulkProductResponse getProducts(@Nullable @RequestParam UUID idGreaterThan, @NotNull Integer limit) {
        return productController.getPageOfProducts(idGreaterThan, limit);
    }

    @PutMapping("/reserve")
    @ResponseBody
    public ProductResponse reserveProduct(@RequestBody @Valid ReserveProductParams reserveProductParams) {
        return productController.reserveProduct(reserveProductParams);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") @NotNull UUID id) {
        productController.deleteProduct(id);
    }



}
