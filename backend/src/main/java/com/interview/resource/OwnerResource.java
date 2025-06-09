package com.interview.resource;

import com.interview.api.OwnerApi;
import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.dto.PageResponseDTO;
import com.interview.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  public ResponseEntity<PageResponseDTO<OwnerDTO>> getOwners(final int page, final int size) {
    final Page<OwnerDTO> result = ownerService.getOwners(PageRequest.of(page, size));
    final PageResponseDTO<OwnerDTO> response = toPageResponseDTO(result);
    return ResponseEntity.ok(response);
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

  private <T> PageResponseDTO<T> toPageResponseDTO(final Page<T> page) {
    return PageResponseDTO.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }
}
