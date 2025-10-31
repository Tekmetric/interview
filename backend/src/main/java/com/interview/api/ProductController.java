package com.interview.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.interview.api.mapper.ProductMapper;
import com.interview.api.model.ProductDto;
import com.interview.dao.model.Product;
import com.interview.exception.ProductNotFoundException;
import com.interview.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/api/product/{id}")
    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "404", description = "Product does not exist or has been deleted")
	public ProductDto getById(@PathVariable final long id,
			@RequestParam(name = "includeDeleted", defaultValue = "false") final boolean includeDeleted) {
		final Product product = productService.getById(id);
		if (!includeDeleted && product.getDeletedDate() != null) {
			throw new ProductNotFoundException("Product " + id + " is not available");
		}
		return ProductMapper.INSTANCE.productToDto(product);
	}

	@GetMapping("/api/product")
    @Operation(summary = "Get products using pagination")
	public Page<ProductDto> getPage(final Pageable pageable,
			@RequestParam(name = "includeDeleted", defaultValue = "false") final boolean includeDeleted) {
		return productService.getPage(pageable, includeDeleted).map(ProductMapper.INSTANCE::productToDto);
	}

	@PostMapping("/api/product")
    @Operation(summary = "Create a new product")
	public ProductDto create(@RequestBody ProductDto input) {
		final Product source = ProductMapper.INSTANCE.dtoToProduct(input);
		final Product created = productService.create(source);
		return ProductMapper.INSTANCE.productToDto(created);
	}

	@PostMapping("/api/product/{id}")
    @Operation(summary = "Update an existing product")
	public ProductDto update(final @PathVariable long id, @RequestBody ProductDto input) {
		checkArgument(input.getId() == null || id == input.getId(), "Product ID is invalid");
		final Product source = ProductMapper.INSTANCE.dtoToProduct(input);
		source.setId(id);
		final Product updated = productService.update(source);
		return ProductMapper.INSTANCE.productToDto(updated);
	}

	@DeleteMapping("/api/product/{id}")
    @Operation(summary = "Delete an existing product")
	public void deleteById(final @PathVariable long id) {
		productService.deleteById(id);
	}
}
