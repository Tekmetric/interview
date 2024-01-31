package com.interview.entity;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "data")
@Table(name = "data")
public class DataRecord {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    @NotEmpty
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "created", nullable = false)
    private Instant created;

    @Column(name = "updated", nullable = false)
    private Instant updated;

}
