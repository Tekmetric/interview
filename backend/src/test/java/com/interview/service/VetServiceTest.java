package com.interview.service;

import com.interview.dto.CreateVetDTO;
import com.interview.dto.VetDTO;
import com.interview.mapper.EntityMapper;
import com.interview.model.Animal;
import com.interview.model.Vet;
import com.interview.repository.VetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private VetServiceImpl vetService;

    // Given: Common test data
    private static final String VET_NAME = "Dr. Smith";
    private static final String VET_SPECIALIZATION = "Surgery";
    private static final String VET_CONTACT = "dr.smith@example.com";
    private static final Long ID = 1L;
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);

    @Test
    void create_ShouldCreateVet_WhenValidDTOProvided() {
        // Given: A valid CreateVetDTO
        final CreateVetDTO createVetDTO = CreateVetDTO.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        final Vet vet = Vet.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        final VetDTO expectedDTO = VetDTO.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        // When: Converting and saving
        when(mapper.toEntity(createVetDTO)).thenReturn(vet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(mapper.toDTO(vet)).thenReturn(expectedDTO);

        final VetDTO result = vetService.create(createVetDTO);

        // Then: The vet should be created
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(VET_NAME);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void findById_ShouldReturnVet_WhenExists() {
        // Given: An existing vet
        final Vet vet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        final VetDTO vetDTO = VetDTO.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        // When: Searching by ID
        when(vetRepository.findById(ID)).thenReturn(Optional.of(vet));
        when(mapper.toDTO(vet)).thenReturn(vetDTO);

        final VetDTO result = vetService.findById(ID);

        // Then: The vet should be found
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Given: A non-existent vet ID
        when(vetRepository.findById(ID)).thenReturn(Optional.empty());

        // When/Then: Searching by ID should throw exception
        assertThatThrownBy(() -> vetService.findById(ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Vet not found");
    }

    @Test
    void findAll_ShouldReturnAllVets() {
        // Given: Multiple vets exist
        final Vet vet1 = Vet.builder()
            .id(1L)
            .name(VET_NAME)
            .build();
        final Vet vet2 = Vet.builder()
            .id(2L)
            .name("Dr. Jones")
            .build();
        final List<Vet> vets = Arrays.asList(vet1, vet2);

        final VetDTO dto1 = VetDTO.builder()
            .id(1L)
            .name(VET_NAME)
            .build();
        final VetDTO dto2 = VetDTO.builder()
            .id(2L)
            .name("Dr. Jones")
            .build();

        // When: Retrieving all vets
        final Page<Vet> vetPage = new PageImpl<>(vets);
        when(vetRepository.findAll(any(Pageable.class))).thenReturn(vetPage);
        when(mapper.toDTO(eq(vet1))).thenReturn(dto1);
        when(mapper.toDTO(eq(vet2))).thenReturn(dto2);

        final Page<VetDTO> results = vetService.findAll(DEFAULT_PAGEABLE);

        // Then: All vets should be returned
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent()).extracting(VetDTO::getName)
            .containsExactlyInAnyOrder(VET_NAME, "Dr. Jones");
        assertThat(results.getTotalElements()).isEqualTo(2);
    }

    @Test
    void update_ShouldUpdateVet_WhenExists() {
        // Given: An existing vet and update data
        final Vet existingVet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        final VetDTO updateDTO = VetDTO.builder()
            .name("Updated " + VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        // When: Updating the vet
        when(vetRepository.findById(ID)).thenReturn(Optional.of(existingVet));
        when(vetRepository.save(any(Vet.class))).thenReturn(existingVet);
        when(mapper.toDTO(existingVet)).thenReturn(updateDTO);

        final VetDTO result = vetService.update(ID, updateDTO);

        // Then: The vet should be updated
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated " + VET_NAME);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void delete_ShouldDeleteVet_WhenExists() {
        // Given: An existing vet
        final Vet vet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .animals(new HashSet<>())
            .build();

        when(vetRepository.findById(ID)).thenReturn(Optional.of(vet));

        // When: Deleting the vet
        vetService.delete(ID);

        // Then: The vet should be saved with removed associations and then deleted
        verify(vetRepository).save(vet);
        verify(vetRepository).deleteById(ID);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given: A non-existent vet
        when(vetRepository.findById(ID)).thenReturn(Optional.empty());

        // When/Then: Deleting should throw exception
        assertThatThrownBy(() -> vetService.delete(ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Vet not found");
    }

    @Test
    void delete_ShouldRemoveAllAnimalAssociations_WhenVetHasAnimals() {
        // Given: A vet with animals
        final Vet vet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .animals(new HashSet<>())
            .build();

        final Animal animal = Animal.builder()
            .id(2L)
            .name("Max")
            .species("Dog")
            .breed("Golden Retriever")
            .dateOfBirth(LocalDate.now())
            .vets(new HashSet<>())
            .build();

        vet.addAnimal(animal);

        when(vetRepository.findById(ID)).thenReturn(Optional.of(vet));

        // When: Deleting the vet
        vetService.delete(ID);

        // Then: The vet should be saved with removed associations before being deleted
        verify(vetRepository).save(vet);
        verify(vetRepository).deleteById(ID);
        assertThat(animal.getVets()).isEmpty();
        assertThat(vet.getAnimals()).isEmpty();
    }
}