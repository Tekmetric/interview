package com.interview.model.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mechanic_shop_history")
public class MechanicShopHistory {

    @Id
    @Column(name = "mechanic_shop_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mechanicShopId;
    @Column(name = "shop_name")
    private String shopName;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private Action action;
    @CreationTimestamp
    private LocalDateTime creationDate;
    @Column(name = "last_update_date", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;
    @Column(name = "modified_by")
    private String modifiedBy;

}
