package com.interview.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record StockMovementCreateRequest(
    @NotNull(message = "Product ID is required")
    Long productId,
    
    @NotNull(message = "Warehouse ID is required")
    Long warehouseId,
    
    @NotBlank(message = "Movement type is required")
    @Pattern(regexp = "IN|OUT", message = "Movement type must be IN or OUT")
    String movementType,
    
    @NotBlank(message = "Movement reason is required")
    @Pattern(regexp = "PURCHASE|SALE|ADJUSTMENT|TRANSFER|RETURN|DAMAGE", 
             message = "Invalid movement reason")
    String movementReason,
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,
    
    @DecimalMin(value = "0.00", message = "Unit cost cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Unit cost must have at most 8 integer digits and 2 decimal places")
    BigDecimal unitCost,
    
    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    String referenceNumber,
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes
) {}