package com.interview.dto;

import java.io.Serializable;
import java.util.Objects;

public class VehicleDTO implements Serializable {

    private Long id;
    private String vin;
    private String make;
    private String model;
    private Integer modelYear;
    private Long customerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
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

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleDTO that = (VehicleDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VehicleDTO{"
            + "id=" + id + ", "
            + "vin='" + vin + "'" + ", "
            + "make='" + make + "'" + ", "
            + "model='" + model + "'" + ", "
            + "modelYear=" + modelYear + ", "
            + "customerId=" + customerId + 
            '}';
    }}
