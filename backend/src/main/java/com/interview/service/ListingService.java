package com.interview.service;

import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ListingService {
  Page<ListingDto> getAllListings(int page, int size);

  ListingDto getListingById(UUID listingId);

  ListingDto createListing(ListingCreationDto listingCreationDto);

  ListingDto putListing(UUID listingId, ListingCreationDto listingCreationDto);

  ListingDto updateListing(UUID listingId, ListingPatchDto listingPatchDto);

  OfferDto createOfferOnListing(UUID listingId, OfferCreationDto offerCreationDto);

  List<OfferDto> getOffersByListingId(UUID listingId);

  void deleteListing(UUID listingId);
}
