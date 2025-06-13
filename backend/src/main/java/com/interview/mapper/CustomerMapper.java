package com.interview.mapper;

import com.interview.dto.CustomerRequestDTO;
import com.interview.dto.CustomerResponseDTO;
import com.interview.dto.CustomerSummaryDTO;
import com.interview.dto.VehicleSummaryDTO;
import com.interview.model.CustomerEntity;
import com.interview.model.VehicleEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between CustomerEntity and DTOs. This interface uses MapStruct to
 * generate the implementation at compile time. It handles the conversion of entities to DTOs and
 * vice versa, including special handling for vehicle counts and repair order counts.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface CustomerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicles", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  void updateEntityFromDTO(@MappingTarget CustomerEntity entity, CustomerRequestDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicles", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  CustomerEntity toEntity(CustomerRequestDTO dto);

  // Ignore vehicles to avoid lazy loading issues
  @Mapping(target = "vehicles", ignore = true)
  CustomerResponseDTO toResponseDTO(CustomerEntity entity);

  CustomerResponseDTO toResponseDTOWithVehicles(CustomerEntity entity);

  // Add explicit mapping method for VehicleEntity to VehicleSummaryDTO
  @Mapping(target = "repairOrderCount", expression = "java(getRepairOrderCount(entity))")
  VehicleSummaryDTO vehicleEntityToVehicleSummaryDTO(VehicleEntity entity);

  @Mapping(target = "vehicleCount", expression = "java(getVehicleCount(entity))")
  CustomerSummaryDTO toSummaryDTO(CustomerEntity entity);

  List<CustomerSummaryDTO> toSummaryDTOList(List<CustomerEntity> entities);

  @Named("getVehicleCount")
  default int getVehicleCount(CustomerEntity entity) {
    return entity.getVehicles() != null ? entity.getVehicles().size() : 0;
  }

  @Named("getRepairOrderCount")
  default int getRepairOrderCount(VehicleEntity entity) {
    return entity.getRepairOrders() != null ? entity.getRepairOrders().size() : 0;
  }

}
