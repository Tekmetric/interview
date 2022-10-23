package com.interview.domain.model.common;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "is_deleted is null")
@Getter
@Setter
public abstract class AbstractAuditingEntity<T> implements Serializable, GenericID<T> {

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now(ZoneId.of("UTC"));;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now(ZoneId.of("UTC"));;

    @Column(name = "is_deleted")
    private String deleted;

    protected String prettyPrintObject(AbstractAuditingEntity<?> object) {
        if (object == null) {
            return "null";
        } else if (!Hibernate.isInitialized(object)) {
            return "[lazy]";
        } else {
            return String.valueOf(object.getId());
        }
    }
}
