package com.interview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_recall")
public class VehicleRecall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int modelYear;

    @Column(nullable = false)
    private String recallDescription;

    @Column(nullable = false)
    private LocalDate recallDate;

    public VehicleRecall(String make, String model, int modelYear, String recallDescription, LocalDate recallDate) {
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.recallDescription = recallDescription;
        this.recallDate = recallDate;
    }

    public VehicleRecall() {

    }

    public long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String manufacturer) {
        this.make = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public String getRecallDescription() {
        return recallDescription;
    }

    public void setRecallDescription(String recallDescription) {
        this.recallDescription = recallDescription;
    }

    public LocalDate getRecallDate() {
        return recallDate;
    }

    public void setRecallDate(LocalDate recallDate) {
        this.recallDate = recallDate;
    }

    @Override
    public String toString() {
        return "VehicleRecall {" +
                "id='" + id + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + modelYear +
                ", recallDescription='" + recallDescription + '\'' +
                ", recallDate=" + recallDate +
                '}';
    }
}
