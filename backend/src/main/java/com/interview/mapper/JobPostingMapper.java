package com.interview.mapper;

import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.model.JobPosting;
import com.interview.model.enums.JobStatus;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobPostingMapper {

    /**
     * Map a request DTO → new entity.
     * Fields managed by the server (id, timestamps) are ignored.
     */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "postedAt",  ignore = true)
    @Mapping(target = "status",    source = "request", qualifiedByName = "resolveStatus")
    @Mapping(target = "remote",    source = "request", qualifiedByName = "resolveRemote")
    @Mapping(target = "currency",  source = "request", qualifiedByName = "resolveCurrency")
    JobPosting toEntity(JobPostingRequest request);

    /**
     * Apply an update request onto an existing entity (partial-safe — null fields are skipped).
     * Timestamps and id are never overwritten by client data.
     */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "postedAt",  ignore = true)
    void updateEntity(JobPostingRequest request, @MappingTarget JobPosting entity);

    /** Map an entity → response DTO. */
    JobPostingResponse toResponse(JobPosting entity);

    // ── Named qualifiers ──────────────────────────────────────

    @Named("resolveStatus")
    default JobStatus resolveStatus(JobPostingRequest request) {
        return request.status() != null ? request.status() : JobStatus.DRAFT;
    }

    @Named("resolveRemote")
    default Boolean resolveRemote(JobPostingRequest request) {
        return request.remote() != null ? request.remote() : Boolean.FALSE;
    }

    @Named("resolveCurrency")
    default String resolveCurrency(JobPostingRequest request) {
        return request.currency() != null ? request.currency().toUpperCase() : "USD";
    }

    //todo check

    // ── AfterMapping: set postedAt when transitioning to ACTIVE ──

    @AfterMapping
    default void handlePostedAt(JobPostingRequest request, @MappingTarget JobPosting entity) {
        if (request.status() == JobStatus.ACTIVE && entity.getPostedAt() == null) {
            entity.setPostedAt(LocalDateTime.now());
        }
    }
}
