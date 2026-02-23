package com.interview.dto;

import com.interview.entity.EmploymentStatus;
import com.interview.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Employee response")
public class EmployeeResponse {

    @Schema(description = "Unique identifier")
    private Long id;

    @Schema(description = "Version for optimistic locking")
    private Long version;

    @Schema(description = "First name")
    private String firstname;

    @Schema(description = "Last name")
    private String lastname;

    @Schema(description = "Hired date (YYYY-MM-DD)")
    private LocalDate hiredDate;

    @Schema(description = "Gender (display: Male, Female, Non-Binary, Other)")
    private Gender gender;

    @Schema(description = "Employment status (display: Active, Terminated, On Leave, Suspended)")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Termination date if applicable (YYYY-MM-DD)")
    private LocalDate termDate;

    @Schema(description = "Yearly salary")
    private BigDecimal yearlySalary;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            Long id,
            Long version,
            String firstname,
            String lastname,
            LocalDate hiredDate,
            Gender gender,
            EmploymentStatus employmentStatus,
            LocalDate termDate,
            BigDecimal yearlySalary
    ) {
        this.id = id;
        this.version = version;
        this.firstname = firstname;
        this.lastname = lastname;
        this.hiredDate = hiredDate;
        this.gender = gender;
        this.employmentStatus = employmentStatus;
        this.termDate = termDate;
        this.yearlySalary = yearlySalary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public LocalDate getTermDate() {
        return termDate;
    }

    public void setTermDate(LocalDate termDate) {
        this.termDate = termDate;
    }

    public BigDecimal getYearlySalary() {
        return yearlySalary;
    }

    public void setYearlySalary(BigDecimal yearlySalary) {
        this.yearlySalary = yearlySalary;
    }
}
