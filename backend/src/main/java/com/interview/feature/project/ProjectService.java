package com.interview.feature.project;

import jakarta.persistence.EntityNotFoundException;
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
        final var project = new Project();
        project.setName(projectDTO.name());
        project.setDescription(projectDTO.description());
        project.setStatus(ProjectStatus.PLANNED);
        return ProjectDTO.toDTO(projectRepository.save(project));
    }

    public ProjectDTO save(final ProjectDTO projectDTO) {
        final var isNew = !projectRepository.existsByUid(projectDTO.uid());
        final var project = isNew ? new Project() : projectRepository.findByUid(projectDTO.uid());
        project.setUid(projectDTO.uid());
        project.setName(projectDTO.name());
        project.setDescription(projectDTO.description());
        project.setStatus(projectDTO.status());
        return ProjectDTO.toDTO(projectRepository.save(project));
    }

    public Page<ProjectDTO> findAll(final Pageable page) {
        return this.projectRepository.findAll(page).map(ProjectDTO::toDTO);
    }

    public ProjectDTO findByUid(final String projectUid) {
        if (!projectRepository.existsByUid(projectUid)) {
            throw new EntityNotFoundException("Project with uid: %s not found".formatted(projectUid));
        }
        return ProjectDTO.toDTO(projectRepository.findByUid(projectUid));
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
        if (!projectRepository.existsByUid(projectUid)) {
            throw new EntityNotFoundException("Project with uid: %s not found".formatted(projectUid));
        }
        this.projectRepository.deleteByUid(projectUid);
    }
}
