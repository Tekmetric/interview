package com.interview.mappers;

import org.mapstruct.Mapping;

@Mapping(target = "createdBy", ignore = true)
@Mapping(target = "createdDate", ignore = true)
@Mapping(target = "lastModifiedBy", ignore = true)
@Mapping(target = "lastModifiedDate", ignore = true)
public @interface MappingAuditIgnore {
}
