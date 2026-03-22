package com.interview.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.interview.persistence.entity.Customer;
import com.interview.persistence.entity.embedded.Address;
import com.interview.persistence.entity.embedded.EmploymentDetails;
import com.interview.dto.request.CreateCustomerRequest;
import com.interview.dto.request.UpdateCustomerRequest;
import com.interview.dto.request.embedded.AddressRequest;
import com.interview.dto.request.embedded.EmploymentDetailsRequest;
import com.interview.dto.response.CustomerResponse;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<Customer, CustomerResponse, CreateCustomerRequest, UpdateCustomerRequest> {

    @Mapping(target = "street",           source = "address.street")
    @Mapping(target = "city",             source = "address.city")
    @Mapping(target = "state",            source = "address.state")
    @Mapping(target = "zipCode",          source = "address.zipCode")
    @Mapping(target = "employmentStatus", source = "employmentDetails.employmentStatus")
    @Mapping(target = "employerName",     source = "employmentDetails.employerName")
    @Mapping(target = "annualIncome",     source = "employmentDetails.annualIncome")
    @Mapping(target = "dateLastModified", source = "dateUpdated")
    @Override
    CustomerResponse toResponse(final Customer entity);

    @AfterMapping
    default void maskSsn(@MappingTarget final CustomerResponse.CustomerResponseBuilder response, final Customer entity) {
        final String ssn = entity.getSsn();
        if (ssn != null && ssn.length() == 11) {
            response.ssn("***-**-" + ssn.substring(7));
        }
    }

    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "dateCreated",       ignore = true)
    @Mapping(target = "dateUpdated",       ignore = true)
    @Mapping(target = "version",           ignore = true)
    @Mapping(target = "address",           source = "address")
    @Mapping(target = "employmentDetails", source = "employmentDetails")
    @Override
    Customer toEntity(final CreateCustomerRequest request);

    Address toAddress(final AddressRequest request);

    EmploymentDetails toEmploymentDetails(final EmploymentDetailsRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "dateUpdated", ignore = true)
    @Mapping(target = "version",     ignore = true)
    @Mapping(target = "ssn",         ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Override
    void updateEntity(final UpdateCustomerRequest request, @MappingTarget final Customer entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(final AddressRequest request, @MappingTarget final Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmploymentDetails(final EmploymentDetailsRequest request, @MappingTarget final EmploymentDetails details);
}
