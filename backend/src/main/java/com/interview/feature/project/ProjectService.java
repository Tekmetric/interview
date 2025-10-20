package com.interview.feature.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectDTO create(final ProjectDTO projectDTO) {
        return ProjectDTO.toDTO(projectRepository.save(ProjectDTO.toEntity(projectDTO)));
    }

    public ProjectDTO save(final ProjectDTO projectDTO) {
        final var isNew = projectDTO.uid() == null || projectDTO.uid().isBlank() || !projectRepository.existsByUid(projectDTO.uid());
        final var project = isNew ? new Project() : projectRepository.findByUid(projectDTO.uid());
        project.setUid(projectDTO.uid());
        project.setName(projectDTO.name());
        project.setDescription(projectDTO.description());
        project.setStatus(projectDTO.status());
        return ProjectDTO.toDTO(projectRepository.save(project));
    }

    public Page<ProjectDTO> findAll(final Pageable page) {
        return this.projectRepository
            .findAll(page)
            .map(ProjectDTO::toDTO);
    }

    public ProjectDTO findByUid(final String uid) {
        return ProjectDTO.toDTO(projectRepository.findByUid(uid));
    }

    public ProjectDTO start(final String uid) {
        final var project = this.projectRepository.findByUid(uid);
        project.setStatus(ProjectStatus.ACTIVE);
        return ProjectDTO.toDTO(project);
    }

    public ProjectDTO complete(final String uid) {
        final var project = this.projectRepository.findByUid(uid);
        project.setStatus(ProjectStatus.COMPLETED);
        return ProjectDTO.toDTO(project);
    }

    public void deleteByUid(final String projectUid) {
        this.projectRepository.deleteByUid(projectUid);
    }
}
