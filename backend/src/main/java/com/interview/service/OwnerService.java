package com.interview.service;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;

public interface OwnerService {

  OwnerDTO createOwner(final OwnerCreateRequestDTO owner);

  OwnerDTO getOwnerById(final Long id);

  OwnerDTO deleteOwnerById(final Long id);
}
