package com.interview.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class CarDto {

    private Long id;

    @NotBlank(message = "Make is mandatory")
    private String make;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @NotNull(message = "Year is mandatory")
    private Integer modelYear;

    private String color;

    @Size(min = 17, max = 17)
    private String vin;

    private Set<Long> customerIds;

    public CarDto() {
    }

    public CarDto(Long id, String make, String model, Integer modelYear, String color, String vin, Set<Long> customerIds) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.color = color;
        this.vin = vin;
        this.customerIds = customerIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Set<Long> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(Set<Long> customerIds) {
        this.customerIds = customerIds;
    }
}
