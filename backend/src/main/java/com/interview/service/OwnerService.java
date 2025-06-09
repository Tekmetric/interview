package com.interview.service;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.dto.OwnerUpdateRequestDTO;
import com.interview.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface OwnerService {

  OwnerDTO createOwner(final OwnerCreateRequestDTO request);

  OwnerDTO getOwnerById(final Long id);

  OwnerDTO deleteOwnerById(final Long id);

  PageResponseDTO<OwnerDTO> getOwners(final Pageable pageable);

  OwnerDTO updateOwner(final Long id, final OwnerUpdateRequestDTO request);
}
