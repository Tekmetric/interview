package com.interview.service;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.VetDTO;
import com.interview.mapper.EntityMapper;
import com.interview.model.Animal;
import com.interview.model.Employee;
import com.interview.model.Vet;
import com.interview.repository.AnimalRepository;
import com.interview.repository.EmployeeRepository;
import com.interview.repository.VetRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {
    @NonNull
    private final AnimalRepository animalRepository;
    @NonNull
    private final EmployeeRepository employeeRepository;
    @NonNull
    private final VetRepository vetRepository;
    @NonNull
    private final EntityMapper mapper;

    @Override
    public AnimalDTO create(final CreateAnimalDTO animalDTO) {
        log.debug("Creating new animal: {}", animalDTO);
        final Animal animal = mapper.toEntity(animalDTO);
        final Animal savedAnimal = animalRepository.save(animal);
        log.info("Created new animal with id: {}", savedAnimal.getId());
        return mapper.toDTO(savedAnimal);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalDTO findById(final Long id) {
        log.debug("Finding animal by id: {}", id);
        return animalRepository.findById(id)
            .map(mapper::toDTO)
            .orElseThrow(() -> {
                log.error("Animal not found with id: {}", id);
                return new EntityNotFoundException("Animal not found");
            });
    }

    @Override
    public AnimalDTO update(final Long id, final AnimalDTO animalDTO) {
        log.debug("Updating animal with id: {}", id);
        final Animal existingAnimal = animalRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Animal not found with id: {}", id);
                return new EntityNotFoundException("Animal not found");
            });

        // Update basic fields
        existingAnimal.setName(animalDTO.getName());
        existingAnimal.setSpecies(animalDTO.getSpecies());
        existingAnimal.setBreed(animalDTO.getBreed());
        existingAnimal.setDateOfBirth(animalDTO.getDateOfBirth());

        // Update employee if changed
        if (animalDTO.getResponsibleEmployeeId() != null) {
            final Employee employee = employeeRepository.findById(animalDTO.getResponsibleEmployeeId())
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", animalDTO.getResponsibleEmployeeId());
                    return new EntityNotFoundException("Employee not found");
                });
            existingAnimal.setResponsibleEmployee(employee);
        }

        // Update vets if provided
        if (animalDTO.getVetIds() != null) {
            updateAnimalVets(existingAnimal, animalDTO.getVetIds());
        }

        final Animal savedAnimal = animalRepository.save(existingAnimal);
        log.info("Updated animal with id: {}", savedAnimal.getId());
        return mapper.toDTO(savedAnimal);
    }

    private void updateAnimalVets(final Animal animal, final Set<Long> vetIds) {
        log.debug("Updating vets for animal with id: {}", animal.getId());
        final List<Vet> vets = vetIds.stream()
            .map(vetId -> vetRepository.findById(vetId)
                .orElseThrow(() -> {
                    log.error("Vet not found with id: {}", vetId);
                    return new EntityNotFoundException("Vet not found with id: " + vetId);
                }))
            .toList();
        animal.updateVets(new HashSet<>(vets));
        log.debug("Updated vets for animal with id: {}", animal.getId());
    }

    @Override
    public void delete(final Long id) {
        log.debug("Deleting animal with id: {}", id);
        final Animal animal = animalRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Animal not found with id: {}", id);
                return new EntityNotFoundException("Animal not found");
            });

        // Remove all vet associations before deleting
        animal.updateVets(new HashSet<>());
        animalRepository.save(animal);  // Save to update the join table
        animalRepository.deleteById(id);
        log.info("Deleted animal with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VetDTO> findVetsByAnimalId(final Long animalId, final Pageable pageable) {
        log.debug("Finding vets by animal id: {} with pagination", animalId);
        return vetRepository.findByAnimalId(animalId, pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalDTO> findByFilters(final String name, final LocalDate startDate, 
            final LocalDate endDate, final Long employeeId, final Pageable pageable) {
        log.debug("Finding animals with filters - name: {}, startDate: {}, endDate: {}, employeeId: {}", 
            name, startDate, endDate, employeeId);

        // If employeeId is provided, verify the employee exists
        if (employeeId != null) {
            employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new EntityNotFoundException("Employee not found");
                });
        }

        return animalRepository.findByFilters(name, startDate, endDate, employeeId, pageable).map(mapper::toDTO);
    }
}