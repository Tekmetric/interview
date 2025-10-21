package com.interview.feature.project;

import com.interview.util.Pages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectDTO createProject(@RequestBody final ProjectDTO projectDTO) {
        return this.projectService.create(projectDTO);
    }

    @PutMapping
    public ProjectDTO saveProject(@RequestBody final ProjectDTO projectDTO) {
        return this.projectService.save(projectDTO);
    }

    @GetMapping
    public Page<ProjectDTO> findALl(@PageableDefault(Pages.DEFAULT_SIZE) final Pageable page) {
        return projectService.findAll(page);
    }

    @GetMapping("/{uid}")
    public ProjectDTO findById(@PathVariable("uid") final String projectUid) {
        return projectService.findByUid(projectUid);
    }

    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("uid") final String projectUid) {
        projectService.deleteByUid(projectUid);
    }
}
