package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing service packages that customers can subscribe to.
 *
 * <p>Contains package information like name, description, and monthly price.
 * Has a many-to-many relationship with Customer entity (customers can have multiple packages,
 * packages can have multiple subscribers).
 *
 * <p>Implements soft delete functionality through the 'active' field. Inactive packages
 * are not physically deleted but marked as inactive to preserve historical data and
 * existing customer subscriptions.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_packages")
public class ServicePackage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "monthly_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyPrice;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToMany(mappedBy = "subscribedPackages", fetch = FetchType.LAZY)
    private Set<Customer> subscribers = new HashSet<>();

    /**
     * Soft delete - mark package as inactive instead of physical deletion.
     * Preserves historical data and existing customer subscriptions.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Reactivate a previously deactivated package.
     * Allows packages to be made available for new subscriptions again.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Check if package is available for new subscriptions.
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicePackage servicePackage)) {
            return false;
        }
        return Objects.equals(name, servicePackage.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}