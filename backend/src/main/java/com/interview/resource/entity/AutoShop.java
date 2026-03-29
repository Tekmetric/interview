package com.interview.resource.entity;


import javax.persistence.*;

@Entity
@Table(name = "autoshops")
public class AutoShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private int contractYears;

    public AutoShop() {}

    public AutoShop(Long id, String name, String address, String city, String state,int contractYears) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.contractYears = contractYears;
    }

    public Long getId() {
        return id;
    }

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
