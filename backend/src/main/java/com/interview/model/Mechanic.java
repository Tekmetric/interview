package com.interview.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Mechanic {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String specialization;

  @ManyToMany(mappedBy = "mechanics")
  private List<RepairOrder> repairOrders;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public List<RepairOrder> getRepairOrders() {
    return repairOrders;
  }

  public void setRepairOrders(List<RepairOrder> repairOrders) {
    this.repairOrders = repairOrders;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Mechanic mechanic = (Mechanic) o;
    return Objects.equals(id, mechanic.id)
        && Objects.equals(name, mechanic.name)
        && Objects.equals(specialization, mechanic.specialization)
        && Objects.equals(repairOrders, mechanic.repairOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, specialization, repairOrders);
  }

  @Override
  public String toString() {
    return "Mechanic{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", specialization='"
        + specialization
        + '\''
        + ", repairOrders="
        + repairOrders
        + '}';
  }
}
