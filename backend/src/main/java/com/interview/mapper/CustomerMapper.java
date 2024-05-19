package com.interview.mapper;


import com.interview.dto.CustomerDTO;
import com.interview.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);
}
