package com.interview.mapper;

import com.interview.db.entity.RepairService;
import com.interview.dto.RepairServiceDTO;
import com.interview.dto.RepairServiceStatus;
import com.interview.exception.MappingException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between RepairService entity and RepairServiceDTO.
 */
@Component
@NoArgsConstructor
public class RepairServiceMapper {

    public RepairServiceDTO toDto(RepairService entity) {
        if (entity == null) {
            return null;
        }
        
        return RepairServiceDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .vehicleMake(entity.getVehicleMake())
                .vehicleModel(entity.getVehicleModel())
                .vehicleYear(entity.getVehicleYear())
                .licensePlate(entity.getLicensePlate())
                .serviceDescription(entity.getServiceDescription())
                .odometerReading(entity.getOdometerReading())
                .status(mapToStatusEnum(entity.getStatus()))
                .build();
    }

    public RepairService toEntity(RepairServiceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return RepairService.builder()
                .id(dto.getId())
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .vehicleMake(dto.getVehicleMake())
                .vehicleModel(dto.getVehicleModel())
                .vehicleYear(dto.getVehicleYear())
                .licensePlate(dto.getLicensePlate())
                .serviceDescription(dto.getServiceDescription())
                .odometerReading(dto.getOdometerReading())
                .status(mapToStatusString(dto.getStatus()))
                .build();
    }

    private RepairServiceStatus mapToStatusEnum(String statusString) {
        if (statusString == null) {
            return null;
        }
        
        try {
            return RepairServiceStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            // If the status string doesn't match any enum value, throw a custom mapping exception
            throw new MappingException("Invalid status value: " + statusString);
        }
    }

    private String mapToStatusString(RepairServiceStatus statusEnum) {
        return statusEnum != null ? statusEnum.name() : null;
    }
}
