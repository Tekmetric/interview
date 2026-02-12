package com.interview.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.interview.model.dto.request.OfferCreationDto;
import com.interview.model.dto.request.OfferPatchDto;
import com.interview.model.dto.response.OfferDto;
import com.interview.service.OfferService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OfferControllerTest {

  @Mock private OfferService offerService;

  private OfferController controller;

  @BeforeEach
  void setUp() {
    controller = new OfferController(offerService);
  }

  @Test
  void getOfferById_defersToOfferService() {
    final UUID offerId = UUID.randomUUID();
    final OfferDto offerDto = new OfferDto();

    when(offerService.getOfferById(offerId)).thenReturn(offerDto);

    final OfferDto result = controller.getOfferById(offerId);

    verify(offerService).getOfferById(offerId);
    assertSame(offerDto, result);
  }

  @Test
  void putOffer_defersToOfferService() {
    final UUID offerId = UUID.randomUUID();
    final OfferCreationDto creationDto = new OfferCreationDto();
    final OfferDto offerDto = new OfferDto();

    when(offerService.putOffer(offerId, creationDto)).thenReturn(offerDto);

    final OfferDto result = controller.putOffer(offerId, creationDto);

    verify(offerService).putOffer(offerId, creationDto);
    assertSame(offerDto, result);
  }

  @Test
  void updateOffer_defersToOfferService() {
    final UUID offerId = UUID.randomUUID();
    final OfferPatchDto patchDto = new OfferPatchDto();
    final OfferDto offerDto = new OfferDto();

    when(offerService.updateOffer(offerId, patchDto)).thenReturn(offerDto);

    final OfferDto result = controller.updateOffer(offerId, patchDto);

    verify(offerService).updateOffer(offerId, patchDto);
    assertSame(offerDto, result);
  }
}
