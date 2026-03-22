package com.interview.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.interview.persistence.entity.CreditApplication;
import com.interview.persistence.entity.Customer;
import com.interview.dto.request.CreateCreditApplicationRequest;
import com.interview.dto.request.UpdateApplicationStatusRequest;
import com.interview.dto.response.CreditApplicationResponse;

@Mapper(componentModel = "spring")
public interface CreditApplicationMapper
        extends EntityMapper<CreditApplication, CreditApplicationResponse, CreateCreditApplicationRequest, UpdateApplicationStatusRequest> {

    @Mapping(target = "customerId",           source = "customer.id")
    @Mapping(target = "customerName",         expression = "java(buildCustomerName(entity))")
    @Mapping(target = "documentUploadUrls",   ignore = true)
    @Mapping(target = "documentDownloadUrls", ignore = true)
    @Mapping(target = "dateLastModified",     source = "dateUpdated")
    @Override
    CreditApplicationResponse toResponse(final CreditApplication entity);

    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "dateUpdated", ignore = true)
    @Mapping(target = "version",     ignore = true)
    @Mapping(target = "customer",    ignore = true)
    @Mapping(target = "status",      ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "decidedAt",   ignore = true)
    @Mapping(target = "documents",   ignore = true)
    @Override
    CreditApplication toEntity(final CreateCreditApplicationRequest request);

    default String buildCustomerName(final CreditApplication entity) {
        final Customer customer = entity.getCustomer();
        if (customer == null) {
            return null;
        }
        final String first = customer.getFirstName();
        final String last  = customer.getLastName();
        if (first == null) {
            return last;
        }
        if (last  == null) {
            return first;
        }
        return first + " " + last;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",                  ignore = true)
    @Mapping(target = "dateCreated",         ignore = true)
    @Mapping(target = "dateUpdated",         ignore = true)
    @Mapping(target = "version",             ignore = true)
    @Mapping(target = "customer",            ignore = true)
    @Mapping(target = "requestedLoanAmount", ignore = true)
    @Mapping(target = "loanPurpose",         ignore = true)
    @Mapping(target = "monthlyDebt",         ignore = true)
    @Mapping(target = "notes",               ignore = true)
    @Mapping(target = "submittedAt",         ignore = true)
    @Mapping(target = "decidedAt",           ignore = true)
    @Mapping(target = "documents",           ignore = true)
    @Override
    void updateEntity(final UpdateApplicationStatusRequest request, @MappingTarget final CreditApplication entity);
}
