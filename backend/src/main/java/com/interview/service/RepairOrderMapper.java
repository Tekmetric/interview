package com.interview.service;

import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.model.RepairOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = LineItemMapper.class,
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public interface RepairOrderMapper {

  @Mapping(source = "customer.id", target = "customerId")
  RepairOrderSummaryDto toSummaryDto(RepairOrder order);

  @Mapping(source = "customer.id", target = "customerId")
  RepairOrderDetailDto toDetailDto(RepairOrder order);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "lineItems", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  RepairOrder toEntity(CreateRepairOrderCommand command);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "lineItems", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(UpdateRepairOrderCommand command, @MappingTarget RepairOrder order);
}
