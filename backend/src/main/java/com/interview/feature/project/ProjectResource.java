package com.interview.feature.project;

import com.interview.util.Pages;
import com.interview.web.ratelimit.RateLimit;
import com.interview.web.ratelimit.RateLimits;
import com.interview.web.validator.OnCreate;
import com.interview.web.validator.OnUpdate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/projects")
@Validated
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Secured({"ROLE_ADMIN"})
    @RateLimits({
        @RateLimit(capacity = 1000, timeValue = 1, timeUnit = TimeUnit.MINUTES),
        @RateLimit(capacity = 10, timeValue = 10, timeUnit = TimeUnit.SECONDS)
    })
    @PostMapping
    @Validated(OnCreate.class)
    public ProjectDTO createProject(@RequestBody @Valid final ProjectDTO projectDTO) {
        return this.projectService.create(projectDTO);
    }

    @PutMapping
    @Validated(OnUpdate.class)
    public ProjectDTO saveProject(@RequestBody @Valid final ProjectDTO projectDTO) {
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
