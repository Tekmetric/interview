package com.interview.entity;

import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar", length = 64)
    private InventoryType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar", length = 64)
    private InventoryStatus status;

    @Column
    @NotNull
    private String brand;

    @Column
    @NotNull
    private String partName;

    @Column
    @NotNull
    private String partNumber;

    @Column
    @NotNull
    private Integer quantity;

    @Column
    @NotNull
    private String supportEmail;

    @Column
    private Instant deletedAt = null;

    public Inventory(InventoryType type, InventoryStatus status, String brand, String partName, String partNumber, Integer quantity, String supportEmail) {
        this.type = type;
        this.status = status;
        this.brand = brand;
        this.partName = partName;
        this.partNumber = partNumber;
        this.quantity = quantity;
        this.supportEmail = supportEmail;
    }
}
