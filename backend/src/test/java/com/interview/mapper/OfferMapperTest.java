package com.interview.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.interview.model.common.Status;
import com.interview.model.domain.ListingEntity;
import com.interview.model.domain.OfferEntity;
import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {OfferMapperImpl.class})
public class OfferMapperTest {

  @Autowired private OfferMapper offerMapper;

  @Test
  void offerEntity_to_offerDto_mapsFields() {
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);

    final OfferDto offerDto = offerMapper.offerEntity_to_offerDto(offerEntity);

    assertEquals(offerEntity.getOfferId(), offerDto.getOfferId());
    assertEquals(offerEntity.getOfferPrice(), offerDto.getOfferPrice());
    assertEquals(offerEntity.getLenderName(), offerDto.getLenderName());
    assertEquals(offerEntity.getStatus(), offerDto.getStatus());
  }

  @Test
  void offerCreationDto_to_offerEntity() {
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);

    final OfferEntity offerEntity = offerMapper.offerCreationDto_to_offerEntity(offerCreationDto);

    assertEquals(offerCreationDto.getOfferPrice(), offerEntity.getOfferPrice());
    assertEquals(offerCreationDto.getLenderName(), offerEntity.getLenderName());
    assertEquals(offerCreationDto.getStatus(), offerEntity.getStatus());
    assertNull(offerEntity.getOfferId());
    assertNull(offerEntity.getListing());
  }

  @Test
  void offerCreationDto_mergeInto_offerEntity_mergesFields() {
    final OfferCreationDto offerCreationDto = Instancio.create(OfferCreationDto.class);
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final UUID originalOfferId = offerEntity.getOfferId();
    final ListingEntity originalListing = offerEntity.getListing();

    final OfferEntity result =
        offerMapper.offerCreationDto_mergeInto_offerEntity(offerEntity, offerCreationDto);

    assertSame(offerEntity, result);
    assertEquals(offerCreationDto.getOfferPrice(), offerEntity.getOfferPrice());
    assertEquals(offerCreationDto.getLenderName(), offerEntity.getLenderName());
    assertEquals(offerCreationDto.getStatus(), offerEntity.getStatus());
    assertEquals(originalOfferId, offerEntity.getOfferId());
    assertEquals(originalListing, offerEntity.getListing());
  }

  @Test
  void offerPatchDto_patchInto_offerEntity_patchesFieldsOntoEntry_whenAllPopulated() {
    final OfferPatchDto offerPatchDto = Instancio.create(OfferPatchDto.class);
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final UUID originalOfferId = offerEntity.getOfferId();
    final ListingEntity originalListing = offerEntity.getListing();

    final OfferEntity result =
        offerMapper.offerPatchDto_patchInto_offerEntity(offerEntity, offerPatchDto);

    assertSame(offerEntity, result);
    assertEquals(offerPatchDto.getOfferPrice(), offerEntity.getOfferPrice());
    assertEquals(offerPatchDto.getLenderName(), offerEntity.getLenderName());
    assertEquals(offerPatchDto.getStatus(), offerEntity.getStatus());
    assertEquals(originalOfferId, offerEntity.getOfferId());
    assertEquals(originalListing, offerEntity.getListing());
  }

  @Test
  void offerPatchDto_patchInto_offerEntity_patchesFieldsOntoEntry_whenNonePopulated() {
    final OfferPatchDto offerPatchDto = new OfferPatchDto();
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final UUID originalOfferId = offerEntity.getOfferId();
    final Double originalOfferPrice = offerEntity.getOfferPrice();
    final String originalLenderName = offerEntity.getLenderName();
    final Status originalStatus = offerEntity.getStatus();
    final ListingEntity originalListing = offerEntity.getListing();

    final OfferEntity result =
        offerMapper.offerPatchDto_patchInto_offerEntity(offerEntity, offerPatchDto);

    assertSame(offerEntity, result);
    assertEquals(originalOfferId, offerEntity.getOfferId());
    assertEquals(originalOfferPrice, offerEntity.getOfferPrice());
    assertEquals(originalLenderName, offerEntity.getLenderName());
    assertEquals(originalStatus, offerEntity.getStatus());
    assertEquals(originalListing, offerEntity.getListing());
  }

  @Test
  void offerPatchDto_patchInto_offerEntity_patchesFieldsOntoEntry_whenSomePopulated() {
    final OfferPatchDto offerPatchDto = new OfferPatchDto();
    offerPatchDto.setLenderName("new lender name");
    final OfferEntity offerEntity = Instancio.create(OfferEntity.class);
    final UUID originalOfferId = offerEntity.getOfferId();
    final Double originalOfferPrice = offerEntity.getOfferPrice();
    final Status originalStatus = offerEntity.getStatus();
    final ListingEntity originalListing = offerEntity.getListing();

    final OfferEntity result =
        offerMapper.offerPatchDto_patchInto_offerEntity(offerEntity, offerPatchDto);

    assertSame(offerEntity, result);
    assertEquals(originalOfferId, offerEntity.getOfferId());
    assertEquals(originalOfferPrice, offerEntity.getOfferPrice());
    assertEquals(offerPatchDto.getLenderName(), offerEntity.getLenderName());
    assertEquals(originalStatus, offerEntity.getStatus());
    assertEquals(originalListing, offerEntity.getListing());
  }
}
