package com.interview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mechanic_shop")
public class MechanicShop {

    @Id
    @Column(name = "mechanic_shop_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shop_name")
    private String shopName;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    @OneToMany(mappedBy = "mechanicShop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Mechanic> mechanics;
    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;
    @Column(name = "last_update_date", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;

}
