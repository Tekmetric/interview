package com.interview.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public static final String COULD_NOT_FIND_PRODUCT_ID = "Could not find product id: ";

    public ProductNotFoundException(Long productId) {
        super(COULD_NOT_FIND_PRODUCT_ID + productId);
    }
}
