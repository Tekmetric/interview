package com.interview.mapper;

import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferMapper {

  OfferDto offerEntity_to_offerDto(OfferEntity offerEntity);

  OfferEntity offerCreationDto_to_offerEntity(OfferCreationDto offerCreationDto);

  OfferEntity offerCreationDto_mergeInto_offerEntity(
      @MappingTarget OfferEntity offerEntity, OfferCreationDto offerCreationDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  OfferEntity offerPatchDto_patchInto_offerEntity(
      @MappingTarget OfferEntity offerEntity, OfferPatchDto offerPatchDto);
}
