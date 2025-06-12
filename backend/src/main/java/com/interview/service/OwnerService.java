package com.interview.service;

import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface OwnerService {

  OwnerDTO createOwner(final OwnerCreateRequestDTO request);

  OwnerDTO getOwnerById(final Long id);

  OwnerDTO deleteOwnerById(final Long id);

  PageResponseDTO<OwnerDTO> getOwners(final String query, final Pageable pageable);

  OwnerDTO updateOwner(final Long id, final OwnerUpdateRequestDTO request);
}
