package com.interview.mapper;

import com.interview.dto.RepairOrderRequestDTO;
import com.interview.dto.RepairOrderResponseDTO;
import com.interview.dto.RepairOrderSummaryDTO;
import com.interview.model.RepairOrderEntity;
import com.interview.model.VehicleEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between RepairOrderEntity and DTOs. This interface uses MapStruct
 * to generate the implementation at compile time. It handles the conversion of entities to DTOs and
 * vice versa, including special handling for vehicle details.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        VehicleMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface RepairOrderMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicle", source = "vehicle") // Vehicle passed as parameter
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  RepairOrderEntity toEntity(RepairOrderRequestDTO dto, VehicleEntity vehicle);

  // Uses VehicleMapper.toVehicleDetailsDTO automatically
  @Mapping(target = "vehicle", source = "vehicle")
  RepairOrderResponseDTO toResponseDTO(RepairOrderEntity entity);

  RepairOrderSummaryDTO toSummaryDTO(RepairOrderEntity entity);

  List<RepairOrderSummaryDTO> toSummaryDTOList(List<RepairOrderEntity> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vehicle", source = "vehicle") // Vehicle passed as parameter
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  void updateEntityFromDTO(@MappingTarget RepairOrderEntity entity, RepairOrderRequestDTO dto,
      VehicleEntity vehicle);

}
