package com.interview.adapter.out.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AutoPartEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 13645423985020585L;

    @Id
    @GeneratedValue(generator = "UUIDGenerator")
    private UUID id;

    private String name;

    private String description;

    private String manufacturer;

    private Integer quantity;

    @CreatedDate
    private OffsetDateTime createdDate;

    @LastModifiedDate
    private OffsetDateTime lastModifiedDate;
}
