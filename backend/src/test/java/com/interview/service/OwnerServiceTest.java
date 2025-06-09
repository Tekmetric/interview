package com.interview.service;

import static org.assertj.core.api.Assertions.*;

import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OwnerServiceTest {

  private static final EasyRandom easyRandom = new EasyRandom();

  @Autowired private OwnerService ownerService;

  @Test
  void createAndGetOwner() {
    final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);

    final OwnerDTO result = ownerService.createOwner(request);
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getBirthDate()).isEqualTo(request.getBirthDate());
    assertThat(result.getAddress()).isEqualTo(request.getAddress());
    assertThat(result.getPersonalNumber()).isEqualTo(request.getPersonalNumber());

    final OwnerDTO found = ownerService.getOwnerById(result.getId());
    assertThat(found.getName()).isEqualTo(request.getName());
    assertThat(found.getBirthDate()).isEqualTo(request.getBirthDate());
    assertThat(found.getAddress()).isEqualTo(request.getAddress());
    assertThat(found.getPersonalNumber()).isEqualTo(request.getPersonalNumber());
  }

  @Test
  void deleteOwnerByIdRemovesOwner() {
    final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);

    final OwnerDTO ownerDTO = ownerService.createOwner(request);
    ownerService.deleteOwnerById(ownerDTO.getId());

    assertThatThrownBy(() -> ownerService.getOwnerById(ownerDTO.getId()))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void getOwnersReturnsPagedResults() {
    for (int i = 0; i < 4; i++) {
      final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);
      ownerService.createOwner(request);
    }
    final PageResponseDTO<OwnerDTO> page = ownerService.getOwners(PageRequest.of(0, 2));
    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(4);
  }

  @Test
  void updateOwnerUpdatesFields() {
    final OwnerCreateRequestDTO createRequest = easyRandom.nextObject(OwnerCreateRequestDTO.class);

    final OwnerDTO ownerDTO = ownerService.createOwner(createRequest);

    final OwnerUpdateRequestDTO updateRequest =
        OwnerUpdateRequestDTO.builder()
            .name("Charlie Updated")
            .birthDate(createRequest.getBirthDate())
            .address(createRequest.getAddress())
            .personalNumber(createRequest.getPersonalNumber())
            .build();

    final OwnerDTO updated = ownerService.updateOwner(ownerDTO.getId(), updateRequest);
    assertThat(updated.getName()).isEqualTo("Charlie Updated");
  }
}
