package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Contact information is required")
    @Email(message = "Contact information must be a valid email address")
    private String contactInformation;
}