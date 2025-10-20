package com.interview.feature.project;

import com.interview.persistence.AuditMetadata;
import jakarta.persistence.Entity;

@Entity
public class Project extends AuditMetadata {
    private String name;
    private String description;
    private ProjectStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
