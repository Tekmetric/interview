package com.interview.integration;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.interview.mapper.ListingMapper;
import com.interview.mapper.OfferMapper;
import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.repo.ListingRepository;
import com.interview.repo.OfferRepository;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
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
public class ListingControllerIntegrationTest {

  private static final URI LISTING_URI = URI.create("/listing");
  private static final UriBuilder LISTING_ID_URI_BUILDER =
      UriComponentsBuilder.fromUri(LISTING_URI).path("/{listingId}");
  private static final UriBuilder LISTING_OFFER_URI_BUILDER =
      UriComponentsBuilder.fromUri(LISTING_URI).path("/{listingId}/offer");

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private OfferRepository offerRepository;
  @Autowired private ListingRepository listingRepository;
  @Autowired private ListingMapper listingMapper;
  @Autowired private OfferMapper offerMapper;

  @BeforeEach
  void setUp() {
    offerRepository.deleteAll();
    listingRepository.deleteAll();
  }

  @Test
  void getListings_returnsEmpty_whenNoListingsExit() {
    final ResponseEntity<CustomPage<ListingDto>> response =
        restTemplate.exchange(
            LISTING_URI, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
  }

  @Test
  void getListings_returnsList_whenListingsPresent() {
    final ListingEntity listingEntity1 =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final ListingEntity listingEntity2 =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final ListingDto listingDto1 = listingMapper.listingEntity_to_listingDto(listingEntity1);
    final ListingDto listingDto2 = listingMapper.listingEntity_to_listingDto(listingEntity2);

    final ResponseEntity<CustomPage<ListingDto>> response =
        restTemplate.exchange(
            LISTING_URI, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getContent().size());
    assertTrue(response.getBody().getContent().contains(listingDto1));
    assertTrue(response.getBody().getContent().contains(listingDto2));
  }

  @Test
  void getListing_returnsListing_whenPresent() {
    final ListingEntity listingEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());

    final ResponseEntity<ListingDto> response =
        restTemplate.getForEntity(
            LISTING_ID_URI_BUILDER.build(listingEntity.getListingId()), ListingDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final ListingDto responseBody = response.getBody();
    compareDtoToEntity(responseBody, listingEntity);
  }

  @Test
  void getListing_returns404_whenListingNotPresent() {
    final ResponseEntity<ListingDto> response =
        restTemplate.getForEntity(
            LISTING_ID_URI_BUILDER.build(UUID.randomUUID()), ListingDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void creatListing_saves_listing() {
    final ListingCreationDto dto = Instancio.create(ListingCreationDto.class);

    final ResponseEntity<ListingDto> response =
        restTemplate.postForEntity(LISTING_URI, dto, ListingDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    final ListingDto responseBody = response.getBody();
    assertNotNull(responseBody.getListingId());
    assertEquals(dto.getAddress(), responseBody.getAddress());
    assertEquals(dto.getAgentName(), responseBody.getAgentName());
    assertEquals(dto.getPropertyType(), responseBody.getPropertyType());
    assertEquals(dto.getListingPrice(), responseBody.getListingPrice());
    assertNotNull(listingRepository.findById(responseBody.getListingId()));
  }

  @Test
  void createListing_isNotIdempotent() {
    final ListingCreationDto dto = Instancio.create(ListingCreationDto.class);

    final ResponseEntity<ListingDto> response1 =
        restTemplate.postForEntity(LISTING_URI, dto, ListingDto.class);
    final ResponseEntity<ListingDto> response2 =
        restTemplate.postForEntity(LISTING_URI, dto, ListingDto.class);

    assertEquals(HttpStatus.CREATED, response1.getStatusCode());
    assertNotNull(response1.getBody());
    assertEquals(HttpStatus.CREATED, response2.getStatusCode());
    assertNotNull(response2.getBody());
    assertEquals(2, listingRepository.findAll().size());
    assertNotEquals(response1.getBody().getListingId(), response2.getBody().getListingId());
  }

  @Test
  void putListing_returns404_whenListingNotPresent() {
    final ListingCreationDto dto = Instancio.create(ListingCreationDto.class);

    final ResponseEntity<ListingDto> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.PUT,
            new HttpEntity<>(dto),
            ListingDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void putListing_updatesListing_whenListingPresent() {
    final ListingEntity originalEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final ListingCreationDto dto = Instancio.create(ListingCreationDto.class);

    final ResponseEntity<ListingDto> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(originalEntity.getListingId()),
            HttpMethod.PUT,
            new HttpEntity<>(dto),
            ListingDto.class);

    final ListingEntity updatedEntity =
        listingRepository.findById(originalEntity.getListingId()).orElseThrow();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final ListingDto responseBody = response.getBody();
    compareDtoToEntity(responseBody, updatedEntity);
  }

  @Test
  void getOffers_returnsEmptyList_whenListingNotPresent() {
    final ResponseEntity<List<OfferDto>> response =
        restTemplate.exchange(
            LISTING_OFFER_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void getOffers_returnsOffers_whenListingPresent() {
    final ListingEntity listingEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final List<OfferEntity> offerEntities =
        Instancio.ofList(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), listingEntity)
            .create();
    listingEntity.setOffers(offerEntities);
    listingRepository.save(listingEntity);

    final List<OfferDto> expected =
        offerRepository.findByListingId(listingEntity.getListingId()).stream()
            .map(offerMapper::offerEntity_to_offerDto)
            .toList();

    final ResponseEntity<List<OfferDto>> response =
        restTemplate.exchange(
            LISTING_OFFER_URI_BUILDER.build(listingEntity.getListingId()),
            HttpMethod.GET,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final List<OfferDto> responseBody = response.getBody();
    assertEquals(expected, responseBody);
  }

  @Test
  void createOffer_returns404_whenListingNotPresent() {
    final UUID listingId = UUID.randomUUID();
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.postForEntity(
            LISTING_OFFER_URI_BUILDER.build(listingId), offerCreationDto, OfferDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void createOffer_createsOffer_whenListingPresent() {
    final ListingEntity listingEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final UUID listingId = listingEntity.getListingId();
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);

    final ResponseEntity<OfferDto> response =
        restTemplate.postForEntity(
            LISTING_OFFER_URI_BUILDER.build(listingId), offerCreationDto, OfferDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final OfferDto responseBody = response.getBody();
    final OfferEntity savedEntity =
        offerRepository.findById(responseBody.getOfferId()).orElseThrow();
    assertEquals(responseBody.getOfferId(), savedEntity.getOfferId());
    assertEquals(responseBody.getOfferPrice(), savedEntity.getOfferPrice());
    assertEquals(responseBody.getLenderName(), savedEntity.getLenderName());
    assertEquals(responseBody.getStatus(), savedEntity.getStatus());
  }

  @Test
  void updateListing_return404_whenListingNotPresent() {
    final UUID listingId = UUID.randomUUID();
    final ListingPatchDto patchDto = Instancio.create(ListingPatchDto.class);

    final ResponseEntity<ListingDto> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(listingId),
            HttpMethod.PATCH,
            new HttpEntity<>(patchDto),
            ListingDto.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void updateListing_updatesListing_whenListingPresent() {
    final ListingEntity originalEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final ListingPatchDto dto = Instancio.create(ListingPatchDto.class);

    final ResponseEntity<ListingDto> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(originalEntity.getListingId()),
            HttpMethod.PATCH,
            new HttpEntity<>(dto),
            ListingDto.class);

    final ListingEntity updatedEntity =
        listingRepository.findById(originalEntity.getListingId()).orElseThrow();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    final ListingDto responseBody = response.getBody();
    compareDtoToEntity(responseBody, updatedEntity);
  }

  @Test
  void deleteListing_return204_whenListingNotPresent() {
    final ResponseEntity<Void> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(UUID.randomUUID()),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void deleteListing_deletesListing_whenListingPresent() {
    final ListingEntity originalEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());

    final ResponseEntity<Void> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(originalEntity.getListingId()),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    assertFalse(listingRepository.existsById(originalEntity.getListingId()));
  }

  @Test
  void deleteListing_deletesListingAndOffers() {
    ListingEntity listingEntity =
        listingRepository.save(
            Instancio.of(ListingEntity.class)
                .ignore(field("listingId"))
                .ignore(field("offers"))
                .create());
    final List<OfferEntity> offerEntities =
        Instancio.ofList(OfferEntity.class)
            .ignore(field(OfferEntity::getOfferId))
            .set(field(OfferEntity::getListing), listingEntity)
            .create();
    listingEntity.setOffers(offerEntities);
    listingEntity = listingRepository.save(listingEntity);

    final ResponseEntity<Void> response =
        restTemplate.exchange(
            LISTING_ID_URI_BUILDER.build(listingEntity.getListingId()),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    assertFalse(listingRepository.existsById(listingEntity.getListingId()));
    listingEntity
        .getOffers()
        .forEach(offerEntity -> assertFalse(offerRepository.existsById(offerEntity.getOfferId())));
  }

  private void compareDtoToEntity(final ListingDto dto, final ListingEntity entity) {
    assertEquals(dto.getListingId(), entity.getListingId());
    assertEquals(dto.getAddress(), entity.getAddress());
    assertEquals(dto.getAgentName(), entity.getAgentName());
    assertEquals(dto.getPropertyType(), entity.getPropertyType());
    assertEquals(dto.getListingPrice(), entity.getListingPrice());
  }

  @Data
  private static class CustomPage<T> {
    private List<T> content;
  }
}
