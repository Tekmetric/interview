package com.interview.mapper;

import com.interview.model.dto.CustomerDTO;
import com.interview.model.entity.Customer;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerMapper {

    Customer toEntity(CustomerDTO dto);
    CustomerDTO toDTO(Customer customer);

    void updateEntityFromDto(CustomerDTO dto, @MappingTarget Customer customer);

    @AfterMapping
    @BeanMapping(builder = @Builder(disableBuilder = true))
    default void linkVehicles(@MappingTarget Customer customer) {
        if (customer.getVehicles() != null) {
            customer.getVehicles().forEach(v -> v.setCustomer(customer));
        }
    }
}