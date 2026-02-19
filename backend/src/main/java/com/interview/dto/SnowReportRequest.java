package com.interview.dto;

import org.springframework.lang.Nullable;

public class SnowReportRequest {

    private String mountainName;

    @Nullable
    private String region;

    private String country;

    private Integer currentSnowTotal;

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
}