package com.interview.common.mapper;

import com.interview.adapter.out.persistence.entities.AutoPartEntity;
import com.interview.model.AutoPart;
import org.springframework.stereotype.Component;

@Component
public class AutoPartMapper {
    public AutoPartEntity mapToEntity(AutoPart dto) {
        AutoPartEntity entity = new AutoPartEntity();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setManufacturer(dto.getManufacturer());
        entity.setQuantity(dto.getQuantity());

        return entity;
    }

    public AutoPart mapToDto(AutoPartEntity entity) {
        AutoPart dto = new AutoPart();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setManufacturer(entity.getManufacturer());
        dto.setQuantity(entity.getQuantity());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedDate(entity.getLastModifiedDate());

        return dto;
    }

    // Method to map updated fields from new entity to existing entity, returns existing entity
    public AutoPartEntity mapUpdatedAutoPartEntityFields(AutoPartEntity updatedAutoPart, AutoPartEntity existingAutoPart) {
        if (updatedAutoPart.getName() != null) {
            existingAutoPart.setName(updatedAutoPart.getName());
        }
        if (updatedAutoPart.getDescription() != null) {
            existingAutoPart.setDescription(updatedAutoPart.getDescription());
        }
        if (updatedAutoPart.getManufacturer() != null) {
            existingAutoPart.setManufacturer(updatedAutoPart.getManufacturer());
        }
        if (updatedAutoPart.getQuantity() != null) {
            existingAutoPart.setQuantity(updatedAutoPart.getQuantity());
        }

        return existingAutoPart;
    }
}
