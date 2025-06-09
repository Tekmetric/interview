package com.interview.resource;

import com.interview.api.OwnerApi;
import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OwnerResource implements OwnerApi {

  private final OwnerService ownerService;

  @Override
  public ResponseEntity<OwnerDTO> getOwnerById(final Long id) {
    final OwnerDTO result = ownerService.getOwnerById(id);
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<PageResponseDTO<OwnerDTO>> getOwners(
      final int page, final int size, final String query) {
    final PageResponseDTO<OwnerDTO> result =
        ownerService.getOwners(query, PageRequest.of(page, size));
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<OwnerDTO> createOwner(final OwnerCreateRequestDTO request) {
    final OwnerDTO result = ownerService.createOwner(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  public ResponseEntity<OwnerDTO> deleteOwnerById(final Long id) {
    final OwnerDTO deletedOwner = ownerService.deleteOwnerById(id);
    return ResponseEntity.ok(deletedOwner);
  }

  @Override
  public ResponseEntity<OwnerDTO> updateOwner(final Long id, final OwnerUpdateRequestDTO request) {
    final OwnerDTO result = ownerService.updateOwner(id, request);
    return ResponseEntity.ok(result);
  }
}
