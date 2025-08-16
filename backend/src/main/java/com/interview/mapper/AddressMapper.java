package com.interview.mapper;

import com.interview.dto.AddressDto;
import com.interview.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    
    @Mapping(target = "id", expression = "java(address.getId() != null ? address.getId().toString() : null)")
    AddressDto toDto(Address address);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Address toEntity(AddressDto addressDto);
}