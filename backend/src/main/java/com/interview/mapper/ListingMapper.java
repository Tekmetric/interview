package com.interview.mapper;

import com.interview.model.domain.ListingEntity;
import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.response.ListingDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = OfferMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListingMapper {

  ListingDto listingEntity_to_listingDto(ListingEntity listingEntity);

  ListingEntity listingCreationD_to_listingEntity(ListingCreationDto listingCreationDto);

  ListingEntity listingCreationDto_mergeInto_listingEntry(
      @MappingTarget ListingEntity listingEntity, ListingCreationDto listingCreationDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  ListingEntity listingPatchDto_patchInto_ListingEntity(
      @MappingTarget ListingEntity listingEntity, ListingPatchDto listingPatchDto);
}
