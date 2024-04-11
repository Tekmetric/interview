package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.interview.utils.CreateOperation;
import com.interview.utils.UpdateOperation;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @NotBlank (message = "The user id is required.", groups = UpdateOperation.class)
    private String id;

    @NotBlank (message = "The username is required.", groups = {UpdateOperation.class, CreateOperation.class})
    private String username;

    @NotNull (message = "The first name is required.", groups = {UpdateOperation.class, CreateOperation.class})
    private String firstName;

    @NotNull (message = "The last name is required.", groups = {UpdateOperation.class, CreateOperation.class})
    private String lastName;

    @NotNull (message = "Password is a mandatory field.", groups = CreateOperation.class)
    private String password;
}
