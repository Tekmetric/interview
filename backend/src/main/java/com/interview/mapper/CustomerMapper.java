package com.interview.mapper;

import com.interview.dto.CustomerDto;
import com.interview.dto.CustomerIdentificationDto;
import com.interview.persistence.CustomerEntity;
import com.interview.service.model.Customer;
import com.interview.service.model.CustomerIdentification;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {VehicleMapper.class})
public interface CustomerMapper {

    Customer dtoToModel(CustomerDto customer);

    CustomerEntity modelToEntity(Customer customer);

    @AfterMapping
    default void setOrder(@MappingTarget CustomerEntity owner) {
        owner.getVehicles().forEach(v -> v.setOwner(owner));
    }

    CustomerDto modelToDto(Customer customer);
    Customer entityToModel(CustomerEntity customer);

    CustomerIdentificationDto modelToIdentificationDto(CustomerIdentification identification);



}
