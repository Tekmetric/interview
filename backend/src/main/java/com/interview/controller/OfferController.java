package com.interview.controller;

import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.service.OfferService;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "offer", produces = "application/json")
@Tag(name = "Offers")
public class OfferController {

  private final OfferService offerService;

  @GetMapping("/{offerId}")
  @Operation(summary = "Get offer by id")
  public OfferDto getOfferById(@PathVariable UUID offerId) {
    return offerService.getOfferById(offerId);
  }

  @PutMapping("/{offerId}")
  @Operation(summary = "Repace offer by id")
  public OfferDto putOffer(
      @PathVariable UUID offerId, @Valid @RequestBody OfferCreationDto offerCreationDto) {
    return offerService.putOffer(offerId, offerCreationDto);
  }

  @PatchMapping("/{offerId}")
  @Operation(summary = "Partially update offer by id")
  public OfferDto updateOffer(
      @PathVariable UUID offerId, @Valid @RequestBody OfferPatchDto offerPatchDto) {
    return offerService.updateOffer(offerId, offerPatchDto);
  }

  @DeleteMapping("/{offerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete offer by id")
  public void deleteOffer(@PathVariable UUID offerId) {
    offerService.deleteOffer(offerId);
  }
}
