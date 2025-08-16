package com.interview.mapper;

import com.interview.dto.CustomerResponse;
import com.interview.dto.RegisterCustomerRequest;
import com.interview.dto.UpdateCustomerRequest;
import com.interview.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

// TODO EXPLAIN: ModelMapper vs MapStruct, generated code
// uses = {AddressMapper.class} enables MapStruct to use AddressMapper inside CustomerMapper for nested object
// relationships.
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    CustomerResponse toDto(Customer customer);
    Customer toEntity(RegisterCustomerRequest request);
    // TODO EXPLAIN: @MappingTarget
    void update(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
