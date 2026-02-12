package com.interview.feature.project;

import com.interview.persistence.AuditMetadata;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Audited
@AuditOverride(forClass = AuditMetadata.class)
public class Project extends AuditMetadata {
    @NotBlank @Size(max = 500)
    private String name;
    @Size(max = 2000)
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
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
