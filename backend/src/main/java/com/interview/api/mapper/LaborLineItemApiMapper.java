package com.interview.api.mapper;

import com.interview.api.request.LaborLineItemRequest;
import com.interview.api.response.LaborLineItemResponse;
import com.interview.domain.LaborLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LaborLineItemApiMapper {

    @Mapping(target = "id", ignore = true)
    LaborLineItem toDomain(LaborLineItemRequest request);

    LaborLineItemResponse toResponse(LaborLineItem laborLineItem);
}
