package com.interview.application.rest.v1.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * User Profile Dto.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto extends AbstractAuditingDto<Long> {
    @ApiModelProperty(value = "The user profile id", position = 1)
    private Long id;
    @ApiModelProperty(value = "The user first name", position = 2)
    private String firstName;
    @ApiModelProperty(value = "The user last name", position = 3)
    private String lastName;
    @ApiModelProperty(value = "The user phone number", position = 4)
    private String phoneNumber;
    @ApiModelProperty(value = "The user birth date", position = 5)
    private LocalDate dateOfBirth;
    @JsonIgnore
    private Long userId;
}
