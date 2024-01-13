package com.interview.products.data;

import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

/**
 * A product to sell in a store
 */
@Entity(name = "products")
public class Product {


    /**
     * Unique random identifier for the product
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;


    /**
     * String, human-readable name of the product
     */
    private String name;

    /**
     * Currency code of the product being sold, i.e USD
     */
    private Currency currency;

    /**
     * Price of the item in the standard units of the currency above, i.e $10.00 -> 10.00
     */
    private BigDecimal price;

    /**
     * Quantity of the item. Null for service based products, non-null for physical products
     */
    @Nullable
    private Integer quantity;

    public Product() {
    }

    public Product(UUID id, String name, Currency currency, BigDecimal price, @Nullable Integer quantity) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrencyCode(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Optional<Integer> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    public void setQuantity(@Nullable Integer quantity) {
        this.quantity = quantity;
    }
}
