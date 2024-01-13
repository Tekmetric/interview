package com.interview.products.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.Nullable;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

/**
 * API representation of an {@link com.interview.products.data.Product}
 */
public class ProductResponse {

    @Id
    private UUID id;

    private String name;

    private Currency currency;

    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal price;

    @Nullable // null for services, non-null for physical products
    private Integer quantity;

    public ProductResponse(UUID id, String name, Currency currency, BigDecimal price, @Nullable Integer quantity) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Optional<Integer> getQuantity() {
        return Optional.ofNullable(quantity);
    }
}
