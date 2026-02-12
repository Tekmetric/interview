package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import java.time.LocalDate;
import java.util.Set;

public class ListOrderDto {
  private Long id;
  private LocalDate orderDate;
  private RepairOrderStatus status;
  private CarDto car;
  private Set<MechanicDto> mechanics;

  public static class CarDto {
    private String licensePlate;
    private String make;
    private String model;

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
  }

  public static class MechanicDto {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public CarDto getCar() {
    return car;
  }

  public void setCar(CarDto car) {
    this.car = car;
  }

  public Set<MechanicDto> getMechanics() {
    return mechanics;
  }

  public void setMechanics(Set<MechanicDto> mechanics) {
    this.mechanics = mechanics;
  }
}
