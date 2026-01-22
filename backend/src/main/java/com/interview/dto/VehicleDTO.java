package com.interview.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class VehicleDTO implements Serializable {

    private Long id;

    @NotBlank(message = "VIN is mandatory")
    @Size(min = 17, max = 17, message = "VIN must be 17 characters")
    private String vin;

    @NotBlank(message = "Make is mandatory")
    private String make;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @NotNull(message = "Model year is mandatory")
    private Integer modelYear;

    @NotNull(message = "Customer ID is mandatory")
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
