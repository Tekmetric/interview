package com.interview.products.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

public class CreateProductParams {

    @NotBlank
    private String name;

    @NotNull
    private Currency currency;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private BigDecimal price;

    @Min(1)
    @Nullable // null for services, non-null for physical products
    private Integer quantity;

    public CreateProductParams(String name, Currency currency, BigDecimal price, @Nullable Integer quantity) {
        this.name = name;
        this.currency = currency;
        this.price = price;
        this.quantity = quantity;
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
