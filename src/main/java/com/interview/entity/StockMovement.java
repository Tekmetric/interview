package com.interview.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_reason", nullable = false)
    private MovementReason movementReason;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_cost")
    private BigDecimal unitCost;
    
    @Column(name = "reference_number", length = 100)
    private String referenceNumber;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Constructors
    public StockMovement() {}
    
    public StockMovement(Product product, Warehouse warehouse, MovementType movementType, 
                        MovementReason movementReason, Integer quantity) {
        this.product = product;
        this.warehouse = warehouse;
        this.movementType = movementType;
        this.movementReason = movementReason;
        this.quantity = quantity;
    }
    
    // Helper methods
    public Integer getSignedQuantity() {
        return movementType == MovementType.IN ? quantity : -quantity;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public MovementReason getMovementReason() { return movementReason; }
    public void setMovementReason(MovementReason movementReason) { this.movementReason = movementReason; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}