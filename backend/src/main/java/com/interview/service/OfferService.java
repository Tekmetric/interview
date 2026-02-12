package com.interview.service;

import com.interview.model.domain.ListingEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;

import java.util.List;
import java.util.UUID;

public interface OfferService {
  List<OfferDto> getOffersByListingId(UUID listingId);

  OfferDto createOfferOnListing(ListingEntity ListingEntity, OfferCreationDto offerCreationDto);

  OfferDto getOfferById(UUID offerId);

  OfferDto putOffer(UUID offerId, OfferCreationDto offerCreationDto);

  OfferDto updateOffer(UUID offerId, OfferPatchDto offerPatchDto);

  void deleteOffer(UUID offerId);
}
