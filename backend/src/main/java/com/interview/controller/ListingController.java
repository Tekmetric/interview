package com.interview.controller;

import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "listing", produces = "application/json")
@Tag(name = "Listings")
public class ListingController {

  private final ListingService listingService;

  @GetMapping
  @Operation(summary = "Get all listings")
  public Page<ListingDto> getAllListings(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return listingService.getAllListings(page, size);
  }

  @GetMapping("/{listingId}")
  @Operation(summary = "Get listing by id")
  public ListingDto getListing(@PathVariable UUID listingId) {
    return listingService.getListingById(listingId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create listing")
  public ListingDto createListing(@Valid @RequestBody ListingCreationDto listingCreationDto) {
    return listingService.createListing(listingCreationDto);
  }

  @PutMapping("/{listingId}")
  @Operation(summary = "Replace listing by id")
  public ListingDto putListing(
      @PathVariable UUID listingId, @Valid @RequestBody ListingCreationDto listingCreationDto) {
    return listingService.putListing(listingId, listingCreationDto);
  }

  @GetMapping("/{listingId}/offer")
  @Operation(summary = "Get all offers on a listing")
  public List<OfferDto> getOffers(@PathVariable UUID listingId) {
    return listingService.getOffersByListingId(listingId);
  }

  @PostMapping("/{listingId}/offer")
  @Operation(summary = "Create offer on a listing")
  public OfferDto createOffer(
      @PathVariable UUID listingId, @Valid @RequestBody OfferCreationDto offerCreationDto) {
    return listingService.createOfferOnListing(listingId, offerCreationDto);
  }

  @PatchMapping("/{listingId}")
  @Operation(summary = "Partially update listing by id")
  public ListingDto updateListing(
      @PathVariable UUID listingId, @Valid @RequestBody ListingPatchDto listingPatchDto) {
    return listingService.updateListing(listingId, listingPatchDto);
  }

  @DeleteMapping("/{listingId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete listing by id")
  public void deleteListing(@PathVariable UUID listingId) {
    listingService.deleteListing(listingId);
  }
}
