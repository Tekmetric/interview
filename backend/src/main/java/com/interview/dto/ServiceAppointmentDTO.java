package com.interview.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class ServiceAppointmentDTO {
    private Long id;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @Future(message = "Appointment date must be in the future")
    private Date appointmentDate;

    private Long customerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
