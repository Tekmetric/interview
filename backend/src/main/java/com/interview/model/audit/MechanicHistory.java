package com.interview.model.audit;

import com.interview.model.Mechanic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Table(name = "mechanic_history")
public class MechanicHistory {

    @Id
    @Column(name = "mechanic_history_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mechanicHistoryId;
    @Column(name = "mechanic_id")
    private Long mechanicId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "mechanic_shop_id")
    private Long mechanicShopId;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private Mechanic.Role role;
    @Enumerated(EnumType.STRING)
    private Action action;
    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;
    @Column(name = "last_update_date", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;
    @Column(name = "modified_by")
    private String modifiedBy;

}
