package com.interview.service;

import com.interview.dto.CreateVetDTO;
import com.interview.dto.VetDTO;
import com.interview.mapper.EntityMapper;
import com.interview.model.Vet;
import com.interview.repository.VetRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VetServiceImpl implements VetService {
    @NonNull
    private final VetRepository vetRepository;
    @NonNull
    private final EntityMapper mapper;

    @Override
    public VetDTO create(final CreateVetDTO vetDTO) {
        log.debug("Creating new vet: {}", vetDTO);
        final Vet vet = mapper.toEntity(vetDTO);
        final Vet savedVet = vetRepository.save(vet);
        log.info("Created new vet with id: {}", savedVet.getId());
        return mapper.toDTO(savedVet);
    }

    @Override
    @Transactional(readOnly = true)
    public VetDTO findById(final Long id) {
        log.debug("Finding vet by id: {}", id);
        return vetRepository.findById(id)
            .map(mapper::toDTO)
            .orElseThrow(() -> {
                log.error("Vet not found with id: {}", id);
                return new EntityNotFoundException("Vet not found");
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VetDTO> findAll(final Pageable pageable) {
        log.debug("Finding all vets with pagination");
        return vetRepository.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    public VetDTO update(final Long id, final VetDTO vetDTO) {
        log.debug("Updating vet with id: {}", id);
        final Vet existingVet = vetRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Vet not found with id: {}", id);
                return new EntityNotFoundException("Vet not found");
            });

        existingVet.setName(vetDTO.getName());
        existingVet.setSpecialization(vetDTO.getSpecialization());
        existingVet.setContactInformation(vetDTO.getContactInformation());

        final Vet savedVet = vetRepository.save(existingVet);
        log.info("Updated vet with id: {}", savedVet.getId());
        return mapper.toDTO(savedVet);
    }

    @Override
    public void delete(final Long id) {
        log.debug("Deleting vet with id: {}", id);
        final Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Vet not found with id: {}", id);
                throw new EntityNotFoundException("Vet not found");
            });

        // Remove all animal associations before deleting
        vet.removeAllAnimals();
        vetRepository.save(vet);  // Save to update the join table
        vetRepository.deleteById(id);
        log.info("Deleted vet with id: {}", id);
    }
}