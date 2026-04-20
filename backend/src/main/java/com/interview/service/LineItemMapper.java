package com.interview.service;

import com.interview.dto.CreateLineItemCommand;
import com.interview.dto.LineItemDto;
import com.interview.model.LineItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public interface LineItemMapper {

  LineItemDto toDto(LineItem lineItem);

  List<LineItemDto> toDtos(List<LineItem> lineItems);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "repairOrder", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  LineItem toEntity(CreateLineItemCommand command);
}
