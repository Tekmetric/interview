package com.interview.model.shop;

import com.interview.model.AbstractAuditingEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shops")
public class Shop extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "image", nullable = false, length = 100)
    private String imageFilename;

    @Column(name = "staff")
    private int staffNumber;

    @Column(name = "avgOrder")
    private BigDecimal avgOrder;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "location", nullable = false, length = 100)
    private String location;

    public Shop(UUID id, String title, String imageFilename, int staffNumber, BigDecimal avgOrder, String location) {
        this.id = id;
        this.title = title;
        this.imageFilename = imageFilename;
        this.staffNumber = staffNumber;
        this.avgOrder = avgOrder;
        this.location = location;
    }

    public Shop() { super();}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public int getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    public BigDecimal getAvgOrder() {
        return avgOrder;
    }

    public void setAvgOrder(BigDecimal avgOrder) {
        this.avgOrder = avgOrder;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shop)) {
            return false;
        }
        return id != null && id.equals(((Shop) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
