package com.interview.mappers;

import com.interview.dto.CustomerDto;
import com.interview.dto.RegisterCustomerRequest;
import com.interview.dto.UpdateCustomerRequest;
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
