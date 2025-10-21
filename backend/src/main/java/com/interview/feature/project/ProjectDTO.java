package com.interview.feature.project;

import com.interview.web.validator.OnCreate;
import com.interview.web.validator.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record ProjectDTO(
    @Null(groups = OnCreate.class) String uid,
    @NotBlank @Size(max = 500) String name,
    @Size(max = 2000) String description,
    @Null(groups = OnCreate.class)
    @NotNull(groups = OnUpdate.class)
    ProjectStatus status) {

    public static ProjectDTO toDTO(Project project) {
        return new ProjectDTO(project.getUid(), project.getName(), project.getDescription(), project.getStatus());
    }
}
