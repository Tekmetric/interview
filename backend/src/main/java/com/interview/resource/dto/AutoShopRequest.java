package com.interview.resource.dto;

import javax.validation.constraints.NotBlank;

public class AutoShopRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    private int contractYears;

    public AutoShopRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getContractYears() {
        return contractYears;
    }

    public void setContractYears(int contractYears) {
        this.contractYears = contractYears;
    }
}
