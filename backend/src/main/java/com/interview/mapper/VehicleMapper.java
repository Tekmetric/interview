package com.interview.mapper;

import com.interview.dto.CustomerSummaryDTO;
import com.interview.dto.VehicleDetailsDTO;
import com.interview.dto.VehicleRequestDTO;
import com.interview.dto.VehicleResponseDTO;
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
 * Mapper interface for converting between VehicleEntity and DTOs. This interface uses MapStruct to
 * generate the implementation at compile time. It handles the conversion of entities to DTOs and
 * vice versa, including special handling for customer details and repair order counts.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface VehicleMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customer", source = "customer") // Customer passed as parameter
  @Mapping(target = "repairOrders", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  VehicleEntity toEntity(VehicleRequestDTO dto, CustomerEntity customer);

  // Uses CustomerMapper.toSummaryDTO automatically
  @Mapping(target = "customer", source = "customer")
  @Mapping(target = "repairOrders", ignore = true)
  VehicleResponseDTO toResponseDTO(VehicleEntity entity);

  @Mapping(target = "vehicleCount", expression = "java(getVehicleCount(entity))")
  CustomerSummaryDTO toSummaryDTO(CustomerEntity entity);

  @Mapping(target = "repairOrders", source = "repairOrders")
  VehicleResponseDTO toResponseDTOWithRepairOrders(VehicleEntity entity);

  @Mapping(target = "repairOrderCount", expression = "java(getRepairOrderCount(entity))")
  VehicleSummaryDTO toSummaryDTO(VehicleEntity entity);

  List<VehicleSummaryDTO> toSummaryDTOList(List<VehicleEntity> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customer", source = "customer") // Customer passed as parameter
  @Mapping(target = "repairOrders", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  void updateEntityFromDTO(@MappingTarget VehicleEntity entity, VehicleRequestDTO dto,
      CustomerEntity customer);

  VehicleDetailsDTO toVehicleDetailsDTO(VehicleEntity entity);

  @Named("getRepairOrderCount")
  default int getRepairOrderCount(VehicleEntity entity) {
    return entity.getRepairOrders() != null ? entity.getRepairOrders().size() : 0;
  }

  @Named("getVehicleCount")
  default int getVehicleCount(CustomerEntity entity) {
    return entity.getVehicles() != null ? entity.getVehicles().size() : 0;
  }

}
