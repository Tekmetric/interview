package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import java.time.LocalDate;
import java.util.Set;

public class CreateOrderDto {
  private LocalDate orderDate;
  private RepairOrderStatus status;
  private CarDto car;
  private Set<Long> mechanicIds;

  public static class CarDto {
    private Long id;
    private String licensePlate;
    private String make;
    private String model;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
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

  public Set<Long> getMechanicIds() {
    return mechanicIds;
  }

  public void setMechanicIds(Set<Long> mechanicIds) {
    this.mechanicIds = mechanicIds;
  }
}
