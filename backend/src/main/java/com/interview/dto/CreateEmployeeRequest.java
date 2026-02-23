package com.interview.dto;

import com.interview.entity.EmploymentStatus;
import com.interview.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request body for creating an employee")
public class CreateEmployeeRequest {

    @Schema(description = "First name", example = "Jane", required = true, maxLength = 255)
    @NotBlank(message = "First name is required")
    @Size(max = 255)
    private String firstname;

    @Schema(description = "Last name", example = "Doe", required = true, maxLength = 255)
    @NotBlank(message = "Last name is required")
    @Size(max = 255)
    private String lastname;

    @Schema(description = "Hired date (YYYY-MM-DD)", example = "2024-01-15", required = true)
    @NotNull(message = "Hired date is required")
    @PastOrPresent(message = "Hired date must be today or in the past")
    private LocalDate hiredDate;

    @Schema(description = "Gender", required = true, allowableValues = { "Male", "Female", "Non-Binary", "Other" })
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Schema(description = "Employment status", required = true, allowableValues = { "Active", "Terminated", "On Leave", "Suspended" })
    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Termination date if applicable (YYYY-MM-DD)", example = "2024-06-30")
    @PastOrPresent(message = "Term date must be today or in the past")
    private LocalDate termDate;

    @Schema(description = "Yearly salary (positive, max 2 decimals)", example = "75000.00", required = true)
    @NotNull(message = "Yearly salary is required")
    @DecimalMin(value = "0", inclusive = false, message = "Yearly salary must be positive")
    @Digits(integer = 17, fraction = 2, message = "Yearly salary must have at most 2 decimal places")
    private BigDecimal yearlySalary;

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
