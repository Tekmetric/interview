package com.interview.mapper;

import com.interview.domain.Supplier;
import com.interview.dto.SupplierDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDTO modelToDto(Supplier supplier);
}
