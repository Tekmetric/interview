package com.interview.mapper;

import com.interview.dto.VehicleDTO;
import com.interview.entity.VehicleEntity;

public class VehicleMapper {

    public static VehicleDTO toDTO(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        VehicleDTO dto = new VehicleDTO();
        dto.setId(entity.getId());
        dto.setMake(entity.getMake());
        dto.setModel(entity.getModel());
        dto.setModelYear(entity.getModelYear());
        dto.setVin(entity.getVin());
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
        }
        return dto;
    }

    public static VehicleEntity toEntity(VehicleDTO dto) {
        if (dto == null) {
            return null;
        }
        VehicleEntity entity = new VehicleEntity();
        entity.setMake(dto.getMake());
        entity.setModel(dto.getModel());
        entity.setModelYear(dto.getModelYear());
        entity.setVin(dto.getVin());
        return entity;
    }

    public static void updateEntityFromDTO(VehicleDTO dto, VehicleEntity entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setMake(dto.getMake());
        entity.setModel(dto.getModel());
        entity.setModelYear(dto.getModelYear());
        entity.setVin(dto.getVin());
    }
}
