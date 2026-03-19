package com.interview.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
public class RepairOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  private LocalDate orderDate;

  @Enumerated(EnumType.STRING)
  private RepairOrderStatus status;

  @ManyToMany
  @JoinTable(
      name = "repair_order_mechanic",
      joinColumns = @JoinColumn(name = "repair_order_id"),
      inverseJoinColumns = @JoinColumn(name = "mechanic_id"))
  private Set<Mechanic> mechanics;

  @ManyToOne
  @JoinColumn(name = "car_id")
  private Car car;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public LocalDate getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDate orderDate) {
    this.orderDate = orderDate;
  }

  public RepairOrderStatus getStatus() {
    return status;
  }

  public void setStatus(RepairOrderStatus status) {
    this.status = status;
  }

  public Set<Mechanic> getMechanics() {
    return mechanics;
  }

  public void setMechanics(Set<Mechanic> mechanics) {
    this.mechanics = mechanics;
  }

  public Car getCar() {
    return car;
  }

  public void setCar(Car car) {
    this.car = car;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RepairOrder that = (RepairOrder) o;
    return Objects.equals(id, that.id)
        && Objects.equals(orderDate, that.orderDate)
        && Objects.equals(version, that.version)
        && status == that.status
        && Objects.equals(mechanics, that.mechanics)
        && Objects.equals(car, that.car);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, orderDate, status, mechanics, car);
  }

  @Override
  public String toString() {
    return "RepairOrder{"
        + "id="
        + id
        + ", orderDate="
        + orderDate
        + ", status="
        + status
        + ", mechanics="
        + mechanics
        + ", version="
        + version
        + ", car="
        + car
        + '}';
  }
}
