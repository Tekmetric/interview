package com.interview.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  private String licensePlate;
  private String make;
  private String model;

  @OneToMany(mappedBy = "car")
  private List<RepairOrder> repairOrders;

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

  public String getLicensePlate() {
    return licensePlate;
  }

  public void setLicensePlate(String licensePlate) {
    this.licensePlate = licensePlate;
  }

  public String getMake() {
    return make;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
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
    Car car = (Car) o;
    return Objects.equals(id, car.id)
        && Objects.equals(licensePlate, car.licensePlate)
        && Objects.equals(make, car.make)
        && Objects.equals(model, car.model)
        && Objects.equals(version, car.version)
        && Objects.equals(repairOrders, car.repairOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, licensePlate, make, model, repairOrders);
  }

  @Override
  public String toString() {
    return "Car{"
        + "id="
        + id
        + ", licensePlate='"
        + licensePlate
        + '\''
        + ", make='"
        + make
        + '\''
        + ", model='"
        + model
        + '\''
        + ", version="
        + version
        + ", repairOrders="
        + repairOrders
        + '}';
  }
}
