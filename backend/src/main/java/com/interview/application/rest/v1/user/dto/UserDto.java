package com.interview.application.rest.v1.user.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.interview.application.configuration.jakson.View;
import com.interview.application.rest.v1.common.dto.AbstractAuditingDto;
import com.interview.domain.model.enums.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto extends AbstractAuditingDto<Long> {
    @ApiModelProperty(value = "The user id", position = 1)
    private Long id;
    @Email
    @NotNull
    @Size(min = 1, max = 50)
    @ApiModelProperty(value = "The user email address", position = 2, required = true)
    private String email;
    @JsonView(View.Internal.class)
    @Size(min = 8, max = 50)
    @NotNull
    @ApiModelProperty(value = "The user password", position = 3, hidden = true)
    private String password;
    @JsonView(View.Internal.class)
    @ApiModelProperty(value = "The user password", position = 4, hidden = true)
    private Role role;
    @Valid
    @ApiModelProperty(value = "The user profile", position = 5)
    private UserProfileDto userProfile;

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", userProfile=" + prettyPrintDTO(userProfile) +
                '}';
    }
}
