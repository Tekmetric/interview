package com.interview.resource;

import com.interview.api.OwnerApi;
import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseEntity<OwnerDTO> createOwner(final OwnerCreateRequestDTO owner) {
    return null;
  }
}
