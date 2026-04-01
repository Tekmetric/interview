package com.interview.service.impl;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.interview.exception.ListingNotFoundException;
import com.interview.mapper.ListingMapper;
import com.interview.model.domain.ListingEntity;
import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.ListingRepository;
import com.interview.service.ListingService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ListingServiceImplTest {

  @Mock private OfferService offerService;
  @Mock private ListingRepository listingRepository;
  @Mock private ListingMapper listingMapper;

  private ListingService listingService;

  @BeforeEach
  void setUp() {
    listingService = new ListingServiceImpl(offerService, listingRepository, listingMapper);
  }

  @Test
  void getAllListings_returnsListings() {
    final ListingEntity listingEntity1 = Instancio.create(ListingEntity.class);
    final ListingEntity listingEntity2 = Instancio.create(ListingEntity.class);
    int pageNumber = 0;
    int pageSize = 10;
    final Pageable pageable = PageRequest.of(pageNumber, pageSize);
    final Page<ListingEntity> page =
        new PageImpl<>(List.of(listingEntity1, listingEntity2), pageable, 2);
    final ListingDto dto1 = Instancio.create(ListingDto.class);
    final ListingDto dto2 = Instancio.create(ListingDto.class);

    when(listingRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(listingMapper.listingEntity_to_listingDto(listingEntity1)).thenReturn(dto1);
    when(listingMapper.listingEntity_to_listingDto(listingEntity2)).thenReturn(dto2);

    final Page<ListingDto> result = listingService.getAllListings(pageNumber, pageSize);

    final List<ListingDto> content = result.getContent();
    assertEquals(2, content.size());
    assertTrue(content.contains(dto1));
    assertTrue(content.contains(dto2));
    verify(listingRepository).findAll(any(Pageable.class));
    verify(listingMapper).listingEntity_to_listingDto(listingEntity1);
    verify(listingMapper).listingEntity_to_listingDto(listingEntity2);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void getAllListings_returnsEmpty_whenNoListingFound() {
    int pageNumber = 0;
    int pageSize = 10;
    final Pageable pageable = PageRequest.of(pageNumber, pageSize);
    final Page<ListingEntity> page = Page.empty(pageable);

    when(listingRepository.findAll(any(Pageable.class))).thenReturn(page);

    final Page<ListingDto> result = listingService.getAllListings(pageNumber, pageSize);

    assertTrue(result.isEmpty());
    verify(listingRepository).findAll(any(Pageable.class));
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void getListingById_returnsListing_whenPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final ListingDto dto = Instancio.create(ListingDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.of(listingEntity));
    when(listingMapper.listingEntity_to_listingDto(listingEntity)).thenReturn(dto);

    final ListingDto result = listingService.getListingById(listingId);

    assertSame(dto, result);
    verify(listingRepository).findById(listingId);
    verify(listingMapper).listingEntity_to_listingDto(listingEntity);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void getListingById_throwsListingNotFound_whenListingNotPresent() {
    final UUID listingId = UUID.randomUUID();

    when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

    assertThrows(ListingNotFoundException.class, () -> listingService.getListingById(listingId));

    verify(listingRepository).findById(listingId);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void createListing_savesCreationRequest() {
    final ListingCreationDto creationDto = Instancio.create(ListingCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final ListingEntity savedListingEntity = Instancio.create(ListingEntity.class);
    final ListingDto dto = Instancio.create(ListingDto.class);

    when(listingMapper.listingCreationD_to_listingEntity(creationDto)).thenReturn(listingEntity);
    when(listingRepository.save(listingEntity)).thenReturn(savedListingEntity);
    when(listingMapper.listingEntity_to_listingDto(savedListingEntity)).thenReturn(dto);

    final ListingDto result = listingService.createListing(creationDto);

    verify(listingMapper).listingCreationD_to_listingEntity(creationDto);
    verify(listingRepository).save(listingEntity);
    verify(listingMapper).listingEntity_to_listingDto(savedListingEntity);
    assertSame(dto, result);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void putListing_throwsListingNotFoundException_whenIdNotPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingCreationDto creationDto = Instancio.create(ListingCreationDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

    assertThrows(
        ListingNotFoundException.class, () -> listingService.putListing(listingId, creationDto));

    verify(listingRepository).findById(listingId);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void putListing_updatesListing_whenIdIsPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingCreationDto creationDto = Instancio.create(ListingCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final ListingEntity savedListingEntity = Instancio.create(ListingEntity.class);
    final ListingDto dto = Instancio.create(ListingDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.of(listingEntity));
    when(listingMapper.listingCreationDto_mergeInto_listingEntry(listingEntity, creationDto))
        .thenReturn(listingEntity);
    when(listingRepository.save(listingEntity)).thenReturn(savedListingEntity);
    when(listingMapper.listingEntity_to_listingDto(savedListingEntity)).thenReturn(dto);

    final ListingDto result = listingService.putListing(listingId, creationDto);

    verify(listingRepository).findById(listingId);
    verify(listingMapper).listingCreationDto_mergeInto_listingEntry(listingEntity, creationDto);
    verify(listingRepository).save(listingEntity);
    verify(listingMapper).listingEntity_to_listingDto(savedListingEntity);
    assertSame(dto, result);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void updateListing_patchesEntity_whenIdIsPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingPatchDto patchDto = Instancio.create(ListingPatchDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final ListingDto dto = Instancio.create(ListingDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.of(listingEntity));
    when(listingMapper.listingPatchDto_patchInto_ListingEntity(listingEntity, patchDto))
        .thenReturn(listingEntity);
    when(listingRepository.save(listingEntity)).thenReturn(listingEntity);
    when(listingMapper.listingEntity_to_listingDto(listingEntity)).thenReturn(dto);

    final ListingDto result = listingService.updateListing(listingId, patchDto);

    verify(listingRepository).findById(listingId);
    verify(listingMapper).listingPatchDto_patchInto_ListingEntity(listingEntity, patchDto);
    verify(listingRepository).save(listingEntity);
    verify(listingMapper).listingEntity_to_listingDto(listingEntity);
    assertSame(dto, result);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void updateListing_throwsListingNotFoundException_whenListingIsNotPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingPatchDto patchDto = Instancio.create(ListingPatchDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

    assertThrows(
        ListingNotFoundException.class, () -> listingService.updateListing(listingId, patchDto));
  }

  @Test
  void createOfferOnListing_passeReferenceToOfferService() {
    final UUID listingId = UUID.randomUUID();
    final OfferCreationDto creationDto = Instancio.create(OfferCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final OfferDto offerDto = Instancio.create(OfferDto.class);

    when(listingRepository.getReferenceById(listingId)).thenReturn(listingEntity);
    when(offerService.createOfferOnListing(listingEntity, creationDto)).thenReturn(offerDto);

    final OfferDto result = listingService.createOfferOnListing(listingId, creationDto);

    assertSame(offerDto, result);
    verify(listingRepository).getReferenceById(listingId);
    verify(offerService).createOfferOnListing(listingEntity, creationDto);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void createOfferOnListing_rethrowsListingNotFoundException_whenListingIsNotPresent() {
    final UUID listingId = UUID.randomUUID();
    final OfferCreationDto creationDto = Instancio.create(OfferCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);

    when(listingRepository.getReferenceById(listingId)).thenReturn(listingEntity);
    when(offerService.createOfferOnListing(listingEntity, creationDto))
        .thenThrow(DataIntegrityViolationException.class);

    assertThrows(
        ListingNotFoundException.class,
        () -> listingService.createOfferOnListing(listingId, creationDto));

    verify(listingRepository).getReferenceById(listingId);
    verify(offerService).createOfferOnListing(listingEntity, creationDto);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void getOffersByListingId_throwsListingNotFoundException_whenListingIsNotPresent() {
    final UUID listingId = UUID.randomUUID();

    when(listingRepository.findById(listingId)).thenReturn(Optional.empty());

    assertThrows(
        ListingNotFoundException.class, () -> listingService.getOffersByListingId(listingId));

    verify(listingRepository).findById(listingId);
    verifyNoMoreInteractions(listingRepository, listingMapper);
  }

  @Test
  void getOffersByListingId_returnsOffers_whenListingIsPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingEntity listingEntity =
        Instancio.of(ListingEntity.class)
            .set(field(ListingEntity::getListingId), listingId)
            .create();
    final List<OfferDto> offerDtos = Instancio.createList(OfferDto.class);

    when(listingRepository.findById(listingId)).thenReturn(Optional.of(listingEntity));
    when(offerService.getOffersByListingId(listingId)).thenReturn(offerDtos);

    final List<OfferDto> result = listingService.getOffersByListingId(listingId);

    assertSame(offerDtos, result);
  }

  @Test
  void deleteListing_deletesByID() {
    final UUID listingId = UUID.randomUUID();

    listingService.deleteListing(listingId);
    verify(listingRepository).deleteById(listingId);
  }
}
