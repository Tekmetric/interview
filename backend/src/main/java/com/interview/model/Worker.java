package com.interview.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "workers", indexes = {
        @Index(name = "idx_worker_name", columnList = "name")
})
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class) // Automatically handles timestamps
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA spec requirement, protected for safety
@AllArgsConstructor
@Builder
public class Worker implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker)) return false;
        Worker worker = (Worker) o;
        return id != null && id.equals(worker.getId());
    }
}