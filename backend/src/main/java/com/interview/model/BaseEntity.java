package com.interview.model;

import com.interview.constants.BaseDbFieldConstants;
import com.interview.enums.AccountStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Base entity class with default fields for all entities.
 * All entities should extend this class.
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = BaseDbFieldConstants.CREATED_AT_COLUMN_NAME, nullable = false, updatable = false)
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = BaseDbFieldConstants.UPDATED_AT_COLUMN_NAME)
    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Column(name = BaseDbFieldConstants.STATUS_COLUMN_NAME)
    private String status;

    @Column(name = BaseDbFieldConstants.PREVIOUS_STATUS_COLUMN_NAME)
    private String previousStatus;

    /**
     * Initialize default values for the entity.
     * Called before saving to ensure required fields have default values.
     */
    @PrePersist
    public void initializeDefaults() {
        if (this.status == null) {
            this.status = AccountStatusEnum.PENDING.getValue();
        }
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Update the timestamp fields.
     * Called before saving to ensure timestamps are current.
     */
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = OffsetDateTime.now();
    }
}

