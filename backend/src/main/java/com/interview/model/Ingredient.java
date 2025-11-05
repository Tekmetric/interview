package com.interview.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredients")
@Schema(description = "Ingredient entity representing a kitchen inventory item")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier of the ingredient", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Name of the ingredient", example = "Fresh Tomatoes", required = true)
    private String name;

    @Column(nullable = false)
    @Schema(description = "Category of the ingredient", example = "Vegetables", required = true)
    private String category;

    @Column(nullable = false)
    @Schema(description = "Current quantity in stock", example = "25.0", required = true)
    private Double quantity;

    @Column(nullable = false)
    @Schema(description = "Unit of measurement", example = "kg", required = true)
    private String unit;

    @Column(name = "minimum_stock", nullable = false)
    @Schema(description = "Minimum stock level before reordering", example = "15.0", required = true)
    private Double minimumStock;

    @Column(precision = 10, scale = 2)
    @Schema(description = "Price per unit", example = "3.50")
    private BigDecimal pricePerUnit;

    @Schema(description = "Supplier name", example = "Local Farm Direct")
    private String supplier;

    @Column(name = "expiration_date")
    @Schema(description = "Expiration date of the ingredient", example = "2025-12-31", type = "string", format = "date")
    private LocalDate expirationDate;

    @Column(name = "requires_refrigeration", nullable = false)
    @Schema(description = "Whether the ingredient requires refrigeration", example = "true", required = true)
    private Boolean requiresRefrigeration;

    @Column(name = "last_updated")
    @Schema(description = "Timestamp of last update", example = "2025-11-04T20:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastUpdated;

    // Constructors
    public Ingredient() {
        this.lastUpdated = LocalDateTime.now();
        this.requiresRefrigeration = false; // Default to false
    }

    public Ingredient(String name, String category, Double quantity, String unit, 
                     Double minimumStock, BigDecimal pricePerUnit, String supplier, LocalDate expirationDate) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.minimumStock = minimumStock;
        this.pricePerUnit = pricePerUnit;
        this.supplier = supplier;
        this.expirationDate = expirationDate;
        this.lastUpdated = LocalDateTime.now();
        this.requiresRefrigeration = false; // Default to false
    }

    public Ingredient(String name, String category, Double quantity, String unit, 
                     Double minimumStock, BigDecimal pricePerUnit, String supplier, 
                     LocalDate expirationDate, Boolean requiresRefrigeration) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.minimumStock = minimumStock;
        this.pricePerUnit = pricePerUnit;
        this.supplier = supplier;
        this.expirationDate = expirationDate;
        this.requiresRefrigeration = requiresRefrigeration;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Double minimumStock) {
        this.minimumStock = minimumStock;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getRequiresRefrigeration() {
        return requiresRefrigeration;
    }

    public void setRequiresRefrigeration(Boolean requiresRefrigeration) {
        this.requiresRefrigeration = requiresRefrigeration;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Helper method to check if ingredient is low in stock
    @Schema(description = "Indicates if the ingredient quantity is at or below minimum stock level", example = "false")
    public boolean isLowStock() {
        return this.quantity <= this.minimumStock;
    }
}
