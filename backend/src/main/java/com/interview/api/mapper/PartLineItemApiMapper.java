package com.interview.api.mapper;

import com.interview.api.request.PartLineItemRequest;
import com.interview.api.response.PartLineItemResponse;
import com.interview.domain.PartLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartLineItemApiMapper {

    @Mapping(target = "id", ignore = true)
    PartLineItem toDomain(PartLineItemRequest request);

    PartLineItemResponse toResponse(PartLineItem partLineItem);
}
