package com.interview.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.interview.model.Status;
import com.interview.utils.CreateOperation;
import com.interview.utils.UpdateOperation;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {

    @NotNull(message = "The id can not be empty.", groups = UpdateOperation.class)
    private Long id;

    @Size(min = 8, message = "The title has to have at least 8 characters.", groups = {CreateOperation.class, UpdateOperation.class})
    private String title;

    @Size(min = 10, message = "The title has to have at least 10 characters.", groups = {CreateOperation.class, UpdateOperation.class})
    private String description;

    private UserDTO requester;

    private UserDTO assignee;

    private Status status;
}