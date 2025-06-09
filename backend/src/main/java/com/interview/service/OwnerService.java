package com.interview.service;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OwnerService {

  OwnerDTO createOwner(final OwnerCreateRequestDTO owner);

  OwnerDTO getOwnerById(final Long id);

  OwnerDTO deleteOwnerById(final Long id);

  Page<OwnerDTO> getOwners(final Pageable pageable);
}
