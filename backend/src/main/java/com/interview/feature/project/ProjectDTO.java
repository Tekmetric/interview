package com.interview.feature.project;

public record ProjectDTO(
    String uid,
    String name,
    String description,
    ProjectStatus status) {

    public static ProjectDTO toDTO(Project project) {
        return new ProjectDTO(project.getUid(), project.getName(), project.getDescription(), project.getStatus());
    }

    public static Project toEntity(ProjectDTO projectDTO) {
        final var project = new Project();
        project.setName(projectDTO.name);
        project.setDescription(projectDTO.description);
        project.setStatus(ProjectStatus.PLANNED);
        return project;
    }

}
