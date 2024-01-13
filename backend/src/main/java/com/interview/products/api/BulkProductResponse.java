package com.interview.products.api;

import java.util.List;

/**
 * API response for a page of {@link ProductResponse}
 */
public class BulkProductResponse {

    /**
     * List of products for the fetched page, ordered by id
     */
    List<ProductResponse> products;

    /**
     * true if there is another page to fetch
     */
    boolean hasNext;

    public BulkProductResponse(List<ProductResponse> products, boolean hasNext) {
        this.products = products;
        this.hasNext = hasNext;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
