package com.interview.mapper;

import com.interview.dto.ServiceJobDTO;
import com.interview.model.ServiceJob;
import com.interview.model.Vehicle;

public class ServiceJobMapper {

    public static ServiceJobDTO toDto(ServiceJob serviceJob) {
        if (serviceJob == null) {
            return null;
        }
        ServiceJobDTO dto = new ServiceJobDTO();
        dto.setId(serviceJob.getId());
        dto.setDescription(serviceJob.getDescription());
        dto.setCreationDate(serviceJob.getCreationDate());
        dto.setStatus(serviceJob.getStatus());
        dto.setCost(serviceJob.getCost());
        if (serviceJob.getVehicle() != null) {
            dto.setVehicleId(serviceJob.getVehicle().getId());
        }
        return dto;
    }

    public static ServiceJob toEntity(ServiceJobDTO dto) {
        if (dto == null) {
            return null;
        }
        ServiceJob serviceJob = new ServiceJob();
        serviceJob.setId(dto.getId());
        serviceJob.setDescription(dto.getDescription());
        serviceJob.setCreationDate(dto.getCreationDate());
        serviceJob.setStatus(dto.getStatus());
        serviceJob.setCost(dto.getCost());
        // Note: The Vehicle entity is not set here. It should be set in the service layer.
        return serviceJob;
    }
}
