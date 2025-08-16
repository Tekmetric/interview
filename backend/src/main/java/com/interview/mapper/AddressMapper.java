package com.interview.mapper;

import com.interview.dto.AddressResponse;
import com.interview.dto.CreateAddressRequest;
import com.interview.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "id", expression = "java(address.getId() != null ? address.getId().toString() : null)")
    AddressResponse toDto(Address address);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Address toEntity(CreateAddressRequest request);
}