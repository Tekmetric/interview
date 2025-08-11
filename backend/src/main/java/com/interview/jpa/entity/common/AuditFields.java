package com.interview.jpa.entity.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.interview.jpa.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
public class AuditFields {

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    protected LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    protected User createdBy;

    @UpdateTimestamp
    @Column(name = "updated_date")
    protected LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected User updatedBy;
}
