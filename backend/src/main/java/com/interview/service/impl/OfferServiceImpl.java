package com.interview.service.impl;

import com.interview.exception.OfferNotFoundException;
import com.interview.mapper.OfferMapper;
import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.OfferRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.interview.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OfferServiceImpl implements OfferService {

  private final OfferRepository offerRepository;
  private final OfferMapper offerMapper;

  @Override
  public List<OfferDto> getOffersByListingId(final UUID listingId) {
    return offerRepository.findByListingId(listingId).stream()
        .map(offerMapper::offerEntity_to_offerDto)
        .collect(Collectors.toList());
  }

    @Override
    public OfferDto createOfferOnListing(final ListingEntity ListingEntity, final OfferCreationDto offerCreationDto) {
        final OfferEntity offerEntity = offerMapper.offerCreationDto_to_offerEntity(offerCreationDto);
        offerEntity.setListing(ListingEntity);

        return offerMapper.offerEntity_to_offerDto(offerRepository.save(offerEntity));
    }

  @Override
  public OfferDto getOfferById(final UUID offerId) {
    return offerRepository
        .findById(offerId)
        .map(offerMapper::offerEntity_to_offerDto)
        .orElseThrow(() -> new OfferNotFoundException(offerId));
  }

  @Override
  public OfferDto putOffer(final UUID offerId, final OfferCreationDto offerCreationDto) {
    return offerRepository
        .findById(offerId)
        .map(o -> offerMapper.offerCreationDto_mergeInto_offerEntity(o, offerCreationDto))
        .map(offerRepository::save)
        .map(offerMapper::offerEntity_to_offerDto)
        .orElseThrow(() -> new OfferNotFoundException(offerId));
  }

  @Override
  public OfferDto updateOffer(final UUID offerId, final OfferPatchDto offerPatchDto) {
    return offerRepository
        .findById(offerId)
        .map(o -> offerMapper.offerPatchDto_patchInto_offerEntity(o, offerPatchDto))
        .map(offerRepository::save)
        .map(offerMapper::offerEntity_to_offerDto)
        .orElseThrow(() -> new OfferNotFoundException(offerId));
  }

  @Override
  public void deleteOffer(final UUID offerId) {
    offerRepository.deleteById(offerId);
  }
}
