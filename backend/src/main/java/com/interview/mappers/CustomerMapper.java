package com.interview.mappers;

import com.interview.dtos.CustomerDto;
import com.interview.dtos.RegisterCustomerRequest;
import com.interview.dtos.UpdateCustomerRequest;
import com.interview.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

// TODO EXPLAIN: ModelMapper vs MapStruct, generated code
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
    Customer toEntity(RegisterCustomerRequest request);
    // TODO EXPLAIN: @MappingTarget
    void update(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
