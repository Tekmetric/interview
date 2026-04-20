package com.interview.service;

import com.interview.dto.LineItemDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.model.LineItem;
import com.interview.model.RepairOrder;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepairOrderMapper {

  @Mapping(source = "customer.id", target = "customerId")
  RepairOrderSummaryDto toSummaryDto(RepairOrder order);

  @Mapping(source = "customer.id", target = "customerId")
  RepairOrderDetailDto toDetailDto(RepairOrder order);

  LineItemDto toLineItemDto(LineItem lineItem);

  List<LineItemDto> toLineItemDtos(List<LineItem> lineItems);
}
