package com.interview.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.interview.model.common.PropertyType;
import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.ListingCreationDto;
import com.interview.model.dto.request.ListingPatchDto;
import com.interview.model.dto.response.ListingDto;
import com.interview.model.dto.response.OfferDto;
import java.util.List;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ContextConfiguration(classes = {ListingMapperImpl.class})
public class ListingMapperTest {

  @MockitoBean private OfferMapper offerMapper;

  @Autowired private ListingMapper listingMapper;

  @Test
  void listingEntity_to_listingDto_mapsFieldsAndNotOffers() {
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);

    when(offerMapper.offerEntity_to_offerDto(any(OfferEntity.class))).thenReturn(new OfferDto());

    final ListingDto listingDto = listingMapper.listingEntity_to_listingDto(listingEntity);

    assertEquals(listingEntity.getListingId(), listingDto.getListingId());
    assertEquals(listingEntity.getAddress(), listingDto.getAddress());
    assertEquals(listingEntity.getAgentName(), listingDto.getAgentName());
    assertEquals(listingEntity.getPropertyType(), listingDto.getPropertyType());
    assertEquals(listingEntity.getListingPrice(), listingDto.getListingPrice());
  }

  @Test
  void listingCreationD_to_listingEntity_mapsFields() {
    final ListingCreationDto creationDto = Instancio.create(ListingCreationDto.class);

    final ListingEntity listingEntity =
        listingMapper.listingCreationD_to_listingEntity(creationDto);

    assertEquals(creationDto.getAddress(), listingEntity.getAddress());
    assertEquals(creationDto.getAgentName(), listingEntity.getAgentName());
    assertEquals(creationDto.getPropertyType(), listingEntity.getPropertyType());
    assertEquals(creationDto.getListingPrice(), listingEntity.getListingPrice());
    assertNull(listingEntity.getOffers());
    assertNull(listingEntity.getListingId());
  }

  @Test
  void listingCreationDto_mergeInto_listingEntry_mergesFieldsOntoEntry() {
    final ListingCreationDto creationDto = Instancio.create(ListingCreationDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final UUID originalListingId = listingEntity.getListingId();
    final List<OfferEntity> originalOffers = listingEntity.getOffers();

    final ListingEntity result =
        listingMapper.listingCreationDto_mergeInto_listingEntry(listingEntity, creationDto);

    assertSame(listingEntity, result);
    assertEquals(creationDto.getAddress(), listingEntity.getAddress());
    assertEquals(creationDto.getAgentName(), listingEntity.getAgentName());
    assertEquals(creationDto.getPropertyType(), listingEntity.getPropertyType());
    assertEquals(creationDto.getListingPrice(), listingEntity.getListingPrice());
    assertEquals(originalListingId, listingEntity.getListingId());
    assertEquals(originalOffers, listingEntity.getOffers());
  }

  @Test
  void listingPatchDto_patchInto_ListingEntity_patchesFieldsOntoEntry_whenAllPopulated() {
    final ListingPatchDto patchDto = Instancio.create(ListingPatchDto.class);
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final UUID originalListingId = listingEntity.getListingId();
    final List<OfferEntity> originalOffers = listingEntity.getOffers();

    final ListingEntity result =
        listingMapper.listingPatchDto_patchInto_ListingEntity(listingEntity, patchDto);

    assertSame(listingEntity, result);
    assertEquals(patchDto.getAddress(), listingEntity.getAddress());
    assertEquals(patchDto.getAgentName(), listingEntity.getAgentName());
    assertEquals(patchDto.getPropertyType(), listingEntity.getPropertyType());
    assertEquals(patchDto.getListingPrice(), listingEntity.getListingPrice());
    assertEquals(originalListingId, listingEntity.getListingId());
    assertEquals(originalOffers, listingEntity.getOffers());
  }

  @Test
  void listingPatchDto_patchInto_ListingEntity_patchesFieldsOntoEntry_whenNonePopulated() {
    final ListingPatchDto patchDto = new ListingPatchDto();
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final UUID originalListingId = listingEntity.getListingId();
    final String originalAddress = listingEntity.getAddress();
    final String originalAgentName = listingEntity.getAgentName();
    final PropertyType originalPropertyType = listingEntity.getPropertyType();
    final Double originalListingPrice = listingEntity.getListingPrice();
    final List<OfferEntity> originalOffers = listingEntity.getOffers();

    final ListingEntity result =
        listingMapper.listingPatchDto_patchInto_ListingEntity(listingEntity, patchDto);

    assertSame(listingEntity, result);
    assertEquals(originalListingId, listingEntity.getListingId());
    assertEquals(originalAddress, listingEntity.getAddress());
    assertEquals(originalAgentName, listingEntity.getAgentName());
    assertEquals(originalPropertyType, listingEntity.getPropertyType());
    assertEquals(originalListingPrice, listingEntity.getListingPrice());
    assertEquals(originalOffers, listingEntity.getOffers());
  }

  @Test
  void listingPatchDto_patchInto_ListingEntity_patchesFilesOntoEntry_whenSomePopulated() {
    final ListingPatchDto patchDto = new ListingPatchDto();
    patchDto.setAddress("new address");
    final ListingEntity listingEntity = Instancio.create(ListingEntity.class);
    final UUID originalListingId = listingEntity.getListingId();
    final String originalAgentName = listingEntity.getAgentName();
    final PropertyType originalPropertyType = listingEntity.getPropertyType();
    final Double originalListingPrice = listingEntity.getListingPrice();
    final List<OfferEntity> originalOffers = listingEntity.getOffers();

    final ListingEntity result =
        listingMapper.listingPatchDto_patchInto_ListingEntity(listingEntity, patchDto);

    assertSame(listingEntity, result);
    assertEquals(originalListingId, listingEntity.getListingId());
    assertEquals(patchDto.getAddress(), listingEntity.getAddress());
    assertEquals(originalAgentName, listingEntity.getAgentName());
    assertEquals(originalPropertyType, listingEntity.getPropertyType());
    assertEquals(originalListingPrice, listingEntity.getListingPrice());
    assertEquals(originalOffers, listingEntity.getOffers());
  }
}
