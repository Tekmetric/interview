package com.interview.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.interview.exception.OfferNotFoundException;
import com.interview.mapper.OfferMapper;
import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.OfferRepository;
import com.interview.service.OfferService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OfferServiceImplTest {

  @Mock private OfferRepository offerRepository;
  @Mock private OfferMapper offerMapper;

  private OfferService offerService;

  @BeforeEach
  void setUp() {
    offerService = new OfferServiceImpl(offerRepository, offerMapper);
  }

  @Test
  void getOffersByListingId_returnsSetOfOffers_whenListingIsPresent() {
    final UUID listingId = UUID.randomUUID();
    final OfferEntity offerEntity1 = Instancio.create(OfferEntity.class);
    final OfferEntity offerEntity2 = Instancio.create(OfferEntity.class);
    final OfferDto offerDto1 = Instancio.create(OfferDto.class);
    final OfferDto offerDto2 = Instancio.create(OfferDto.class);

    when(offerRepository.findByListingId(listingId))
        .thenReturn(List.of(offerEntity1, offerEntity2));
    when(offerMapper.offerEntity_to_offerDto(offerEntity1)).thenReturn(offerDto1);
    when(offerMapper.offerEntity_to_offerDto(offerEntity2)).thenReturn(offerDto2);

    final List<OfferDto> result = offerService.getOffersByListingId(listingId);

    verify(offerRepository).findByListingId(listingId);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity1);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity2);
    assertEquals(2, result.size());
    assertTrue(result.contains(offerDto1));
    assertTrue(result.contains(offerDto2));
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void getOffersByListingId_returnsEmptySet_whenListingIsNotPresent() {
    final UUID listingId = UUID.randomUUID();

    when(offerRepository.findByListingId(listingId)).thenReturn(List.of());

    final List<OfferDto> result = offerService.getOffersByListingId(listingId);

    verify(offerRepository).findByListingId(listingId);
    assertEquals(0, result.size());
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void getOfferById_returnsOffer_whenOfferIsPresent() {
    final UUID offerId = UUID.randomUUID();
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final OfferDto offerDto = Instancio.create(OfferDto.class);

    when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerEntity));
    when(offerMapper.offerEntity_to_offerDto(offerEntity)).thenReturn(offerDto);

    final OfferDto result = offerService.getOfferById(offerId);

    verify(offerRepository).findById(offerId);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity);
    assertSame(offerDto, result);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void getOfferById_throwsOfferNotFoundException_whenOfferIsNotPresent() {
    final UUID offerId = UUID.randomUUID();

    when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

    assertThrows(OfferNotFoundException.class, () -> offerService.getOfferById(offerId));

    verify(offerRepository).findById(offerId);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void createOfferOnListing_createsOffer() {
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final OfferCreationDto creationDto = Instancio.create(OfferCreationDto.class);
    final OfferEntity offerEntity = spy(Instancio.create(OfferEntity.class));
    final OfferDto offerDto = Instancio.create(OfferDto.class);

    when(offerMapper.offerCreationDto_to_offerEntity(creationDto)).thenReturn(offerEntity);
    when(offerRepository.save(offerEntity)).thenReturn(offerEntity);
    when(offerMapper.offerEntity_to_offerDto(offerEntity)).thenReturn(offerDto);

    final OfferDto result = offerService.createOfferOnListing(listingEntity, creationDto);

    verify(offerMapper).offerCreationDto_to_offerEntity(creationDto);
    verify(offerEntity).setListing(listingEntity);
    verify(offerRepository).save(offerEntity);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity);
    assertSame(offerDto, result);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void putOffer_setsOffer_whenOfferIsPresent() {
    final UUID offerId = UUID.randomUUID();
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);
    final OfferEntity offerEntity = spy(Instancio.create(OfferEntity.class));
    final OfferDto offerDto = Instancio.create(OfferDto.class);

    when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerEntity));
    when(offerMapper.offerCreationDto_mergeInto_offerEntity(offerEntity, offerCreationDto))
        .thenReturn(offerEntity);
    when(offerRepository.save(offerEntity)).thenReturn(offerEntity);
    when(offerMapper.offerEntity_to_offerDto(offerEntity)).thenReturn(offerDto);

    final OfferDto result = offerService.putOffer(offerId, offerCreationDto);

    verify(offerRepository).findById(offerId);
    verify(offerMapper).offerCreationDto_mergeInto_offerEntity(offerEntity, offerCreationDto);
    verify(offerRepository).save(offerEntity);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity);
    assertSame(offerDto, result);
  }

  @Test
  void putOffer_setsOffer_whenOfferIsNotPresent() {
    final UUID offerId = UUID.randomUUID();
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);

    when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

    assertThrows(
        OfferNotFoundException.class, () -> offerService.putOffer(offerId, offerCreationDto));

    verify(offerRepository).findById(offerId);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void updateOffer_patchesOffer_whenPresent() {
    final UUID offerId = UUID.randomUUID();
    final OfferPatchDto offerPatchDto = Instancio.create(OfferPatchDto.class);
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final OfferDto offerDto = Instancio.create(OfferDto.class);

    when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerEntity));
    when(offerMapper.offerPatchDto_patchInto_offerEntity(offerEntity, offerPatchDto))
        .thenReturn(offerEntity);
    when(offerRepository.save(offerEntity)).thenReturn(offerEntity);
    when(offerMapper.offerEntity_to_offerDto(offerEntity)).thenReturn(offerDto);

    final OfferDto result = offerService.updateOffer(offerId, offerPatchDto);

    verify(offerRepository).findById(offerId);
    verify(offerMapper).offerPatchDto_patchInto_offerEntity(offerEntity, offerPatchDto);
    verify(offerRepository).save(offerEntity);
    verify(offerMapper).offerEntity_to_offerDto(offerEntity);
    assertSame(offerDto, result);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }

  @Test
  void updateOffer_throwsOfferNotFoundException_whenOfferIsNotPresent() {
    final UUID offerId = UUID.randomUUID();
    final OfferPatchDto offerPatchDto = Instancio.create(OfferPatchDto.class);

    when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

    assertThrows(
        OfferNotFoundException.class, () -> offerService.updateOffer(offerId, offerPatchDto));

    verify(offerRepository).findById(offerId);
    verifyNoMoreInteractions(offerRepository, offerMapper);
  }
}
