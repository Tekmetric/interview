package com.interview.model.db;

import com.interview.model.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;


@Entity
@Table(name = "job")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_car_id", nullable = false)
    private Car car;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Task> tasks;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Job(Integer id) {
        this.id = id;
    }

    public Job(Car car, JobStatus status, Instant scheduledAt) {
        this.car = car;
        this.status = status;
        this.scheduledAt = scheduledAt;
    }
}
