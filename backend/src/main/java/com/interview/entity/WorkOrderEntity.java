package com.interview.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "work_orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class WorkOrderEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderPartEntity> parts = new ArrayList<>();
}
