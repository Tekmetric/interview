package com.interview.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 500)
    private String location;
    
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
    
    // Constructors
    public Warehouse() {}
    
    public Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}