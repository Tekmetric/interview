package com.interview.service;

import static com.interview.service.ServiceUtils.toPageResponseDTO;

import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.entity.Car;
import com.interview.entity.Owner;
import com.interview.mapper.OwnerMapper;
import com.interview.repository.CarRepository;
import com.interview.repository.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
  private final CarRepository carRepository;
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
    final Owner existingOwner = findOwnerOrThrow(id);
    return ownerMapper.toDto(existingOwner);
  }

  @Override
  public OwnerDTO deleteOwnerById(final Long id) {
    final Owner existingOwner = findOwnerOrThrow(id);
    ownerRepository.delete(existingOwner);

    return ownerMapper.toDto(existingOwner);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponseDTO<OwnerDTO> getOwners(final Pageable pageable) {
    final Page<OwnerDTO> page = ownerRepository.findAll(pageable).map(ownerMapper::toDto);
    return toPageResponseDTO(page);
  }

  @Override
  public OwnerDTO updateOwner(final Long id, final OwnerUpdateRequestDTO request) {
    final Owner existingOwner = findOwnerOrThrow(id);

    final List<Long> carIds = Optional.ofNullable(request.getCarIds()).orElse(List.of());
    if (!carIds.isEmpty()) {
      final List<Car> cars = carRepository.findAllById(carIds);
      validateAllCarIdsExist(carIds, cars);
      cars.forEach(car -> car.setOwner(existingOwner));
      existingOwner.setCars(cars);
    }

    ownerMapper.updateOwnerFromDto(request, existingOwner);

    final Owner savedOwner = ownerRepository.save(existingOwner);

    // Flush the changes so that the updated entity is immediately visible(last update timestamp)
    ownerRepository.flush();
    return ownerMapper.toDto(savedOwner);
  }

  private Owner findOwnerOrThrow(final Long id) {
    return ownerRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
  }

  private void validateAllCarIdsExist(final List<Long> requestedCarIds, final List<Car> foundCars) {
    final Set<Long> requestedIdSet = new HashSet<>(requestedCarIds);
    final Set<Long> foundIdSet = foundCars.stream().map(Car::getId).collect(Collectors.toSet());
    if (!requestedIdSet.equals(foundIdSet)) {
      final Set<Long> missingIds = new HashSet<>(requestedIdSet);
      missingIds.removeAll(foundIdSet);

      final Set<Long> unexpectedIds = new HashSet<>(foundIdSet);
      unexpectedIds.removeAll(requestedIdSet);
      throw new EntityNotFoundException(
          "Cars not found for IDs: "
              + missingIds
              + (unexpectedIds.isEmpty() ? "" : ". Unexpected cars found: " + unexpectedIds));
    }
  }
}
