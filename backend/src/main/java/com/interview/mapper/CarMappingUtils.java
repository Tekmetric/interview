package com.interview.mapper;

import com.interview.entity.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CarMappingUtils {

  @Named("ownerToId")
  default Long ownerToId(final Owner owner) {
    return owner != null ? owner.getId() : null;
  }
}
