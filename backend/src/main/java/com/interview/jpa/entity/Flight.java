package com.interview.jpa.entity;

import com.interview.jpa.entity.common.AuditFields;
import com.interview.jpa.entity.enums.FlightEnum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "flight")
@DynamicUpdate
public class Flight extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plane_id")
    private Plane plane;

    @Column(name = "code", nullable = false)
    private String code;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "departure_airport")
    private String departureAirport;

    @Column(name = "arrival_airport")
    private String arrivalAirport;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "terminal")
    private String terminal;

    @Column(name = "gate")
    private String gate;
}
