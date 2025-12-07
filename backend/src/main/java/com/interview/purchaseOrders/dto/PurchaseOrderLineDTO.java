package com.interview.purchaseOrders.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class PurchaseOrderLineDTO {
    private Long purchaseOrderLineId;
    private Long purchaseOrderId;
    private String sku;
    private String description;
    private String color;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal unitWeight;

    public PurchaseOrderLineDTO(){

    }

    public PurchaseOrderLineDTO(Long purchaseOrderLineId, Long purchaseOrderId, String sku, String description, String color, Integer quantity, BigDecimal unitCost, BigDecimal unitWeight) {
        this.purchaseOrderLineId = purchaseOrderLineId;
        this.purchaseOrderId = purchaseOrderId;
        this.sku = sku;
        this.description = description;
        this.color = color;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.unitWeight = unitWeight;
    }

    public Long getPurchaseOrderLineId() {
        return purchaseOrderLineId;
    }

    public void setPurchaseOrderLineId(Long purchaseOrderLineId) {
        this.purchaseOrderLineId = purchaseOrderLineId;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(BigDecimal unitWeight) {
        this.unitWeight = unitWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderLineDTO that = (PurchaseOrderLineDTO) o;
        return Objects.equals(purchaseOrderLineId, that.purchaseOrderLineId) && Objects.equals(sku, that.sku) && Objects.equals(description, that.description) && Objects.equals(color, that.color) && Objects.equals(quantity, that.quantity) && Objects.equals(unitCost, that.unitCost) && Objects.equals(unitWeight, that.unitWeight);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(purchaseOrderLineId);
    }
}
