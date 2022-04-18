package com.interview.controller;

import com.interview.controller.dto.CommentDto;
import com.interview.controller.dto.ProductDto;
import com.interview.controller.mapper.CommentMapper;
import com.interview.controller.mapper.product.ProductMapper;
import com.interview.controller.mapper.product.ProductWithCommentMapper;
import com.interview.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
public class ProductController {

    private static final String ID = "id";

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductWithCommentMapper productWithCommentMapper;
    private final CommentMapper commentMapper;

    public ProductController(ProductService productService, ProductMapper productMapper, ProductWithCommentMapper productWithCommentMapper, CommentMapper commentMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productWithCommentMapper = productWithCommentMapper;
        this.commentMapper = commentMapper;
    }

    @GetMapping("/products/{id}")
    public ProductDto getProductById(@PathVariable @Valid @Positive Long id) {
        return productWithCommentMapper.modelToDto(productService.findProductById(id));
    }

    @GetMapping("/products")
    public List<ProductDto> getProductsPaginatedSortedById(@RequestParam @Valid @PositiveOrZero int page, @RequestParam @Valid @PositiveOrZero int pageSize) {
        return productMapper.modelToDtos(productService.findProductsPaginated(PageRequest.of(page, pageSize, Sort.by(ID))));
    }

    @PostMapping("/products")
    public ProductDto saveProduct(@Valid @RequestBody ProductDto productDto) {
        return productMapper.modelToDto(productService.saveProduct(productMapper.dtoToModel(productDto)));
    }

    @PostMapping("/products/{id}/comments")
    public ResponseEntity<?> addCommentToProduct(@PathVariable(name = "id") Long productId, @Valid @RequestBody CommentDto commentDto) {
        productService.addCommentToPost(productId, commentMapper.dtoToModel(commentDto));
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/products/{id}")
    public ProductDto updateProduct(@PathVariable @Valid @Positive Long id, @Valid @RequestBody ProductDto productDto) {
        return productMapper.modelToDto(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/products/{id}")
    public Long deleteProduct(@PathVariable @Valid @Positive Long id) {
        productService.deleteById(id);

        return id;
    }
}
