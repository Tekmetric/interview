package com.interview.service.impl;

import com.interview.exception.ListingNotFoundException;
import com.interview.mapper.ListingMapper;
import com.interview.model.domain.ListingEntity;
import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.ListingRepository;
import java.util.List;
import java.util.UUID;

import com.interview.service.ListingService;
import com.interview.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ListingServiceImpl implements ListingService {

  private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "listingId");

  private final OfferService offerService;
  private final ListingRepository listingRepository;
  private final ListingMapper listingMapper;

  @Override
  public Page<ListingDto> getAllListings(int page, int size) {
    final Pageable pageable = PageRequest.of(page, size, DEFAULT_SORT);
    return listingRepository.findAll(pageable).map(listingMapper::listingEntity_to_listingDto);
  }

  @Override
  public ListingDto getListingById(final UUID listingId) {
    return listingRepository
        .findById(listingId)
        .map(listingMapper::listingEntity_to_listingDto)
        .orElseThrow(() -> new ListingNotFoundException(listingId));
  }

  @Override
  public ListingDto createListing(final ListingCreationDto listingCreationDto) {
    // TODO validate incomming request
    final ListingEntity listingEntity =
        listingMapper.listingCreationD_to_listingEntity(listingCreationDto);
    return listingMapper.listingEntity_to_listingDto(listingRepository.save(listingEntity));
  }

  @Override
  public ListingDto putListing(final UUID listingId, final ListingCreationDto listingCreationDto) {
    return listingRepository
        .findById(listingId)
        .map(
            existingListing ->
                listingMapper.listingCreationDto_mergeInto_listingEntry(
                    existingListing, listingCreationDto))
        .map(listingRepository::save)
        .map(listingMapper::listingEntity_to_listingDto)
        .orElseThrow(() -> new ListingNotFoundException(listingId));
  }

  @Override
  public ListingDto updateListing(
          final UUID listingId, final ListingPatchDto listingPatchDto) {
    return listingRepository
        .findById(listingId)
        .map(l -> listingMapper.listingPatchDto_patchInto_ListingEntity(l, listingPatchDto))
        .map(listingRepository::save)
        .map(listingMapper::listingEntity_to_listingDto)
        .orElseThrow(() -> new ListingNotFoundException(listingId));
  }

  @Override
  public OfferDto createOfferOnListing(
          final UUID listingId, final OfferCreationDto offerCreationDto) {
    // TODO handle reference being null
    final ListingEntity listingEntity = listingRepository.getReferenceById(listingId);

    try {
      return offerService.createOfferOnListing(listingEntity, offerCreationDto);
    } catch (DataIntegrityViolationException e) {
      throw new ListingNotFoundException(listingId);
    }
  }

  @Override
  public List<OfferDto> getOffersByListingId(final UUID listingId) {
    final ListingEntity listingEntity =
        listingRepository
            .findById(listingId)
            .orElseThrow(() -> new ListingNotFoundException(listingId));

    return offerService.getOffersByListingId(listingEntity.getListingId());
  }

  @Override
  public void deleteListing(final UUID listingId) {
    listingRepository.deleteById(listingId);
  }
}
