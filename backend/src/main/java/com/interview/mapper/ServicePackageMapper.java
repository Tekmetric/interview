package com.interview.mapper;

import com.interview.dto.ServicePackageRequest;
import com.interview.dto.ServicePackageResponse;
import com.interview.entity.ServicePackage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between ServicePackage entities and DTOs.
 *
 * <p>Handles conversions between ServicePackage entities and
 * ServicePackageRequest/ServicePackageResponse DTOs with automatic code generation.
 */
@Mapper(componentModel = "spring")
public interface ServicePackageMapper {

    /**
     * Convert ServicePackageRequest to ServicePackage entity.
     * Active field will be set to true by default in entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ServicePackage toEntity(ServicePackageRequest request);

    /**
     * Convert ServicePackage entity to ServicePackageResponse.
     * Includes subscriber count for business insights.
     */
    @Named("toResponseWithSubscribers")
    @Mapping(target = "subscriberCount", expression = "java(getSubscriberCount(servicePackage))")
    ServicePackageResponse toResponse(ServicePackage servicePackage);

    /**
     * Convert ServicePackage entity to ServicePackageResponse (without subscriber count).
     * Used for operations where subscribers are not loaded to avoid N+1 queries.
     */
    @Named("toResponseWithoutSubscribers")
    @Mapping(target = "subscriberCount", ignore = true)
    ServicePackageResponse toResponseWithoutSubscribers(ServicePackage servicePackage);

    /**
     * Convert list of ServicePackage entities to list of ServicePackageResponse.
     */
    List<ServicePackageResponse> toResponseList(List<ServicePackage> servicePackages);

    /**
     * Update existing ServicePackage entity with ServicePackageRequest.
     * Preserves ID, active status, subscribers, and audit fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(@MappingTarget ServicePackage existingServicePackage, ServicePackageRequest request);

    /**
     * Helper method to calculate subscriber count.
     * Returns 0 if subscribers collection is null (lazy loading not triggered).
     */
    default Integer getSubscriberCount(ServicePackage servicePackage) {
        if (servicePackage.getSubscribers() == null) {
            return null; // Indicates subscribers not loaded
        }
        return servicePackage.getSubscribers().size();
    }
}