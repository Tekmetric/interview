package com.interview.integration;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.ListingRepository;
import com.interview.repo.OfferRepository;
import java.net.URI;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OfferControllerIntegrationTest {

  private static final URI OFFER_URI = URI.create("/offer");
  private static final UriBuilder OFFER_ID_URI_BUILDER =
      UriComponentsBuilder.fromUri(OFFER_URI).path("/{offerId}");

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private OfferRepository offerRepository;
  @Autowired private ListingRepository listingRepository;

  private ListingEntity existingListing;

  @BeforeEach
  void setUp() {
    offerRepository.deleteAll();
    listingRepository.deleteAll();

    existingListing =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
  }

  @Test
  void getOffById_returns404_whenOfferNotFound() {
    final ResponseEntity<OfferDto> response =
        restTemplate.getForEntity(OFFER_ID_URI_BUILDER.build(UUID.randomUUID()), OfferDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getOffById_returnsOfferDto_whenOfferFound() {
    OfferEntity offerEntity =
        Instancio.of(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), existingListing)
            .create();
    offerEntity = offerRepository.save(offerEntity);
    final UUID offerId = offerEntity.getOfferId();

    final ResponseEntity<OfferDto> response =
        restTemplate.getForEntity(OFFER_ID_URI_BUILDER.build(offerId), OfferDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final OfferDto offerDto = response.getBody();
    compareDtoToEntity(offerDto, offerEntity);
  }

  @Test
  void putOffer_returns404_whenOfferNotFound() {
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.PUT,
            new HttpEntity<>(offerCreationDto),
            OfferDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void putOffer_updatesOffer_whenPresent() {
    OfferEntity offerEntity =
        Instancio.of(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), existingListing)
            .create();
    offerEntity = offerRepository.save(offerEntity);
    final UUID offerId = offerEntity.getOfferId();
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(offerId),
            HttpMethod.PUT,
            new HttpEntity<>(offerCreationDto),
            OfferDto.class);

    final OfferEntity updatedEntity = offerRepository.findById(offerId).orElseThrow();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final OfferDto offerDto = response.getBody();
    compareDtoToEntity(offerDto, updatedEntity);
  }

  @Test
  void updateOffer_returns404_whenOfferNotFound() {
    final OfferPatchDto offerPatchDto = Instancio.create(OfferPatchDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.PATCH,
            new HttpEntity<>(offerPatchDto),
            OfferDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void updateOffer_updatesOffer_whenPresent() {
    OfferEntity offerEntity =
        Instancio.of(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), existingListing)
            .create();
    offerEntity = offerRepository.save(offerEntity);
    final UUID offerId = offerEntity.getOfferId();
    final OfferPatchDto offerPatchDto = Instancio.create(OfferPatchDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(offerId),
            HttpMethod.PATCH,
            new HttpEntity<>(offerPatchDto),
            OfferDto.class);

    final OfferEntity updatedEntity = offerRepository.findById(offerId).orElseThrow();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final OfferDto offerDto = response.getBody();
    compareDtoToEntity(offerDto, updatedEntity);
  }

  @Test
  void deleteOffer_return204_whenOfferNotPresent() {
    final ResponseEntity<Void> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void deleteOffer_deletesOffer_whenPresent() {
    OfferEntity offerEntity =
        Instancio.of(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), existingListing)
            .create();
    offerEntity = offerRepository.save(offerEntity);
    final UUID offerId = offerEntity.getOfferId();

    final ResponseEntity<Void> response =
        restTemplate.exchange(
            OFFER_ID_URI_BUILDER.build(offerId), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertFalse(offerRepository.existsById(offerId));
  }

  private void compareDtoToEntity(final OfferDto dto, final OfferEntity entity) {
    assertEquals(dto.getOfferId(), entity.getOfferId());
    assertEquals(dto.getOfferPrice(), entity.getOfferPrice());
    assertEquals(dto.getLenderName(), entity.getLenderName());
    assertEquals(dto.getStatus(), entity.getStatus());
  }
}
