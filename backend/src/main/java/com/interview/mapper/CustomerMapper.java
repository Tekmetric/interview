package com.interview.mapper;

import com.interview.dto.CustomerResponse;
import com.interview.dto.CreateCustomerRequest;
import com.interview.dto.UpdateCustomerRequest;
import com.interview.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// TODO EXPLAIN: ModelMapper vs MapStruct, generated code
// uses = {AddressMapper.class} enables MapStruct to use AddressMapper inside CustomerMapper for nested object
// relationships.
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    CustomerResponse toDto(Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Customer toEntity(CreateCustomerRequest request);
    
    // TODO EXPLAIN: @MappingTarget
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    void update(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
