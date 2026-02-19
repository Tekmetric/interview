package com.interview.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "snow_report")
public class SnowReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mountainName;

    @Nullable
    private String region;

    private String country;

    private Integer currentSnowTotal;

    private LocalDateTime lastUpdated;

    @PrePersist // Could handle this on the db side with a default value
    @PreUpdate
    void onSave() {
        this.lastUpdated = LocalDateTime.now();
    }

    public SnowReport() {
    }

    public SnowReport(Long id, String mountainName, @Nullable String region, String country,
                      Integer currentSnowTotal, LocalDateTime lastUpdated) {
        this.id = id;
        this.mountainName = mountainName;
        this.region = region;
        this.country = country;
        this.currentSnowTotal = currentSnowTotal;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMountainName() {
        return mountainName;
    }

    public void setMountainName(String mountainName) {
        this.mountainName = mountainName;
    }

    @Nullable
    public String getRegion() {
        return region;
    }

    public void setRegion(@Nullable String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCurrentSnowTotal() {
        return currentSnowTotal;
    }

    public void setCurrentSnowTotal(Integer currentSnowTotal) {
        this.currentSnowTotal = currentSnowTotal;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
