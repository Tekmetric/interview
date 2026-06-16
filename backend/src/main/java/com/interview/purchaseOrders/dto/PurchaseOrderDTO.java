package com.interview.purchaseOrders.dto;

import com.interview.purchaseOrders.model.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PurchaseOrderDTO {
    private Long purchaseOrderId;
    private String supplierName;
    private LocalDate placedOn;
    private LocalDate expectedDelivery;
    private LocalDate actualDelivery;
    private BigDecimal totalCost;
    private BigDecimal totalWeight;
    private PurchaseOrderStatus purchaseOrderStatus;
    private List<PurchaseOrderLineDTO> purchaseOrderLines = new ArrayList<>();

    public PurchaseOrderDTO() {
    }

    public PurchaseOrderDTO(Long purchaseOrderId, String supplierName, LocalDate placedOn, LocalDate expectedDelivery, LocalDate actualDelivery, BigDecimal totalCost, BigDecimal totalWeight, PurchaseOrderStatus purchaseOrderStatus, List<PurchaseOrderLineDTO> purchaseOrderLines) {
        this.purchaseOrderId = purchaseOrderId;
        this.supplierName = supplierName;
        this.placedOn = placedOn;
        this.expectedDelivery = expectedDelivery;
        this.actualDelivery = actualDelivery;
        this.totalCost = totalCost;
        this.totalWeight = totalWeight;
        this.purchaseOrderStatus = purchaseOrderStatus;
        this.purchaseOrderLines = purchaseOrderLines;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getPlacedOn() {
        return placedOn;
    }

    public void setPlacedOn(LocalDate placedOn) {
        this.placedOn = placedOn;
    }

    public LocalDate getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setExpectedDelivery(LocalDate expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }

    public LocalDate getActualDelivery() {
        return actualDelivery;
    }

    public void setActualDelivery(LocalDate actualDelivery) {
        this.actualDelivery = actualDelivery;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public PurchaseOrderStatus getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(PurchaseOrderStatus purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    public List<PurchaseOrderLineDTO> getPurchaseOrderLines() {
        return purchaseOrderLines;
    }

    public void setPurchaseOrderLines(List<PurchaseOrderLineDTO> purchaseOrderLines) {
        this.purchaseOrderLines = purchaseOrderLines;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrderDTO that = (PurchaseOrderDTO) o;
        return Objects.equals(purchaseOrderId, that.purchaseOrderId) && Objects.equals(supplierName, that.supplierName) && Objects.equals(placedOn, that.placedOn) && Objects.equals(expectedDelivery, that.expectedDelivery) && Objects.equals(actualDelivery, that.actualDelivery) && Objects.equals(totalCost, that.totalCost) && Objects.equals(totalWeight, that.totalWeight) && purchaseOrderStatus == that.purchaseOrderStatus && Objects.equals(purchaseOrderLines, that.purchaseOrderLines);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(purchaseOrderId);
    }
}
