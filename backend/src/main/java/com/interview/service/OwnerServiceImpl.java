package com.interview.service;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.entity.Owner;
import com.interview.mapper.OwnerMapper;
import com.interview.repository.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OwnerServiceImpl implements OwnerService {

  private final OwnerRepository ownerRepository;
  private final OwnerMapper ownerMapper;

  @Override
  public OwnerDTO createOwner(final OwnerCreateRequestDTO request) {
    final Owner owner = ownerMapper.toEntity(request);
    final Owner savedOwner = ownerRepository.save(owner);
    return ownerMapper.toDto(savedOwner);
  }

  @Override
  @Transactional(readOnly = true)
  public OwnerDTO getOwnerById(final Long id) {
    final Owner owner =
        ownerRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
    return ownerMapper.toDto(owner);
  }

  @Override
  public OwnerDTO deleteOwnerById(final Long id) {
    final Owner owner =
        ownerRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
    ownerRepository.delete(owner);
    return ownerMapper.toDto(owner);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<OwnerDTO> getOwners(final Pageable pageable) {
    return ownerRepository.findAll(pageable).map(ownerMapper::toDto);
  }
}
