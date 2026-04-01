package com.interview.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.service.ListingService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ListingControllerTest {

  private ListingController controller;

  @Mock private ListingService listingService;

  @BeforeEach
  void setUp() {
    controller = new ListingController(listingService);
  }

  @Test
  void getAllListings_defersToListingService() {
    int pageNumber = 0;
    int pageSize = 10;
    final Pageable pageable = PageRequest.of(pageNumber, pageSize);
    final Page<ListingDto> listings = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(listingService.getAllListings(pageNumber, pageSize)).thenReturn(listings);

    final Page<ListingDto> result = controller.getAllListings(pageNumber, pageSize);

    verify(listingService).getAllListings(pageNumber, pageSize);
    assertSame(listings, result);
  }

  @Test
  void getListing_defersToListingService() {
    final UUID listingId = UUID.randomUUID();
    final ListingDto listingDto = new ListingDto();

    when(listingService.getListingById(listingId)).thenReturn(listingDto);

    final ListingDto result = controller.getListing(listingId);

    verify(listingService).getListingById(listingId);
    assertSame(listingDto, result);
  }

  @Test
  void createListing_defersToListingService() {
    final ListingCreationDto dto = new ListingCreationDto();
    final ListingDto listingDto = new ListingDto();

    when(listingService.createListing(dto)).thenReturn(listingDto);

    final ListingDto result = controller.createListing(dto);

    verify(listingService).createListing(dto);
    assertSame(listingDto, result);
  }

  @Test
  void putListing_defersToListingService() {
    final UUID listingId = UUID.randomUUID();
    final ListingCreationDto dto = new ListingCreationDto();
    final ListingDto listingDto = new ListingDto();

    when(listingService.putListing(listingId, dto)).thenReturn(listingDto);

    final ListingDto result = controller.putListing(listingId, dto);

    verify(listingService).putListing(listingId, dto);
    assertSame(listingDto, result);
  }

  @Test
  void getOffers_defersToListingService() {
    final UUID listingId = UUID.randomUUID();
    final List<OfferDto> offers = Instancio.createList(OfferDto.class);

    when(listingService.getOffersByListingId(listingId)).thenReturn(offers);

    final List<OfferDto> result = controller.getOffers(listingId);

    verify(listingService).getOffersByListingId(listingId);
    assertSame(offers, result);
  }

  @Test
  void createOffer_defersToOfferService() {
    final UUID listingId = UUID.randomUUID();
    final OfferCreationDto creationDto = new OfferCreationDto();
    final OfferDto offerDto = new OfferDto();

    when(listingService.createOfferOnListing(listingId, creationDto)).thenReturn(offerDto);

    final OfferDto result = controller.createOffer(listingId, creationDto);

    verify(listingService).createOfferOnListing(listingId, creationDto);
    assertSame(offerDto, result);
  }

  @Test
  void updateListing_defersToListingService() {
    final UUID listingId = UUID.randomUUID();
    final ListingPatchDto listingPatchDto = new ListingPatchDto();
    final ListingDto listingDto = new ListingDto();

    when(listingService.updateListing(listingId, listingPatchDto)).thenReturn(listingDto);

    final ListingDto result = controller.updateListing(listingId, listingPatchDto);

    verify(listingService).updateListing(listingId, listingPatchDto);
    assertSame(listingDto, result);
  }

  @Test
  void deleteListing_defersToListingService() {
    final UUID listingId = UUID.randomUUID();

    controller.deleteListing(listingId);

    verify(listingService).deleteListing(listingId);
  }
}
