package com.interview.repair_order_line.domain;

import com.interview._infrastructure.domain.AuditedFields;
import com.interview.repair_order.domain.RepairOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "repair_order_lines")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Audited
public class RepairOrderLine  extends AuditedFields  {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private String id ;

    @ManyToOne(targetEntity = RepairOrder.class)
    @JoinColumn(name = "repair_order_id")
    private RepairOrder repairOrder;

    private String description;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    //round to 2 decimal places at the lowest line level to avoid rounding differences when summing
    public BigDecimal getAmount() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepairOrderLine)) {
            return false;
        }
        RepairOrderLine repairOrderLine = (RepairOrderLine) o;

        return this.id != null && this.id.equals(repairOrderLine.getId());
    }

    @Override
    public int hashCode() {
        return this.id != null ? Objects.hash(this.id) : 0;
    }
}
