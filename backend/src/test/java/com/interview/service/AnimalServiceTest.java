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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private VetRepository vetRepository;

    @Mock
    private EntityMapper mapper;

    @InjectMocks
    private AnimalServiceImpl animalService;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String ANIMAL_NAME = "Max";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Golden Retriever";
    private static final String EMPLOYEE_NAME = "John Doe";
    private static final String VET_NAME = "Dr. Smith";
    private static final String VET_SPECIALIZATION = "Surgery";
    private static final String VET_CONTACT = "dr.smith@example.com";
    private static final Long ID = 1L;
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
    private static final Pageable SMALL_PAGE = PageRequest.of(0, 2);
    private static final String PAGINATED_PREFIX = "Max";

    @Test
    void create_ShouldCreateAnimal_WhenValidDTOProvided() {
        // Given: A valid CreateAnimalDTO and corresponding entities
        final CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        final Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        final AnimalDTO expectedDTO = AnimalDTO.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        // When: Converting and saving
        when(mapper.toEntity(createAnimalDTO)).thenReturn(animal);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);
        when(mapper.toDTO(animal)).thenReturn(expectedDTO);

        final AnimalDTO result = animalService.create(createAnimalDTO);

        // Then: The animal should be created with basic fields
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(ANIMAL_NAME);
        assertThat(result.getSpecies()).isEqualTo(ANIMAL_SPECIES);
        assertThat(result.getBreed()).isEqualTo(ANIMAL_BREED);
        assertThat(result.getDateOfBirth()).isEqualTo(TODAY);
        assertThat(result.getResponsibleEmployeeId()).isNull();
        assertThat(result.getVetIds()).isNull();
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void findById_ShouldReturnAnimal_WhenExists() {
        // Given: An existing animal
        final Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        final AnimalDTO animalDTO = AnimalDTO.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        // When: Searching by ID
        when(animalRepository.findById(ID)).thenReturn(Optional.of(animal));
        when(mapper.toDTO(animal)).thenReturn(animalDTO);

        final AnimalDTO result = animalService.findById(ID);

        // Then: The animal should be found
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Given: A non-existent animal ID
        when(animalRepository.findById(ID)).thenReturn(Optional.empty());

        // When/Then: Searching by ID should throw exception
        assertThatThrownBy(() -> animalService.findById(ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Animal not found");
    }

    @Test
    void findByFilters_ShouldReturnAnimals_WhenAllFiltersProvided() {
        // Given: Animals matching all filters
        final Animal animal1 = Animal.builder()
            .id(1L)
            .name("Max")
            .dateOfBirth(TODAY.minusYears(2))
            .build();
        final Animal animal2 = Animal.builder()
            .id(2L)
            .name("Max Jr")
            .dateOfBirth(TODAY.minusYears(1))
            .build();

        final AnimalDTO dto1 = AnimalDTO.builder()
            .id(1L)
            .name("Max")
            .dateOfBirth(TODAY.minusYears(2))
            .build();
        final AnimalDTO dto2 = AnimalDTO.builder()
            .id(2L)
            .name("Max Jr")
            .dateOfBirth(TODAY.minusYears(1))
            .build();

        final Employee employee = Employee.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .build();

        // When: Searching with all filters
        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));
        when(animalRepository.findByFilters(eq("Max"), eq(TODAY.minusYears(3)), eq(TODAY), eq(ID), eq(DEFAULT_PAGEABLE)))
            .thenReturn(new PageImpl<>(Arrays.asList(animal1, animal2)));
        when(mapper.toDTO(animal1)).thenReturn(dto1);
        when(mapper.toDTO(animal2)).thenReturn(dto2);

        final Page<AnimalDTO> result = animalService.findByFilters(
            "Max",
            TODAY.minusYears(3),
            TODAY,
            ID,
            DEFAULT_PAGEABLE
        );

        // Then: Should return filtered animals
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(AnimalDTO::getName)
            .containsExactlyInAnyOrder("Max", "Max Jr");
        verify(animalRepository).findByFilters(eq("Max"), eq(TODAY.minusYears(3)), eq(TODAY), eq(ID), eq(DEFAULT_PAGEABLE));
    }

    @Test
    void findByFilters_ShouldReturnAnimals_WhenOnlyNameProvided() {
        // Given: Animals matching name filter
        final Animal animal = Animal.builder()
            .id(1L)
            .name("Max")
            .build();

        final AnimalDTO dto = AnimalDTO.builder()
            .id(1L)
            .name("Max")
            .build();

        // When: Searching with only name filter
        when(animalRepository.findByFilters(eq("Max"), eq(null), eq(null), eq(null), eq(DEFAULT_PAGEABLE)))
            .thenReturn(new PageImpl<>(Collections.singletonList(animal)));
        when(mapper.toDTO(animal)).thenReturn(dto);

        final Page<AnimalDTO> result = animalService.findByFilters(
            "Max",
            null,
            null,
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Should return animals matching name
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Max");
        verify(animalRepository).findByFilters(eq("Max"), eq(null), eq(null), eq(null), eq(DEFAULT_PAGEABLE));
    }

    @Test
    void findByFilters_ShouldThrowException_WhenEmployeeDoesNotExist() {
        // Given: Non-existent employee ID
        when(employeeRepository.findById(ID)).thenReturn(Optional.empty());

        // When/Then: Searching with invalid employee ID should throw exception
        assertThatThrownBy(() -> animalService.findByFilters(
            null,
            null,
            null,
            ID,
            DEFAULT_PAGEABLE
        ))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Employee not found");
    }

    @Test
    void findVetsByAnimalId_ShouldReturnVets() {
        // Given: Vets assigned to an animal
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

        // When: Searching for vets by animal ID
        final Page<Vet> vetPage = new PageImpl<>(Collections.singletonList(vet));
        when(vetRepository.findByAnimalId(eq(ID), any(Pageable.class))).thenReturn(vetPage);
        when(mapper.toDTO(vet)).thenReturn(vetDTO);

        final Page<VetDTO> results = animalService.findVetsByAnimalId(ID, DEFAULT_PAGEABLE);

        // Then: The vets should be returned
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getName()).isEqualTo(VET_NAME);
        assertThat(results.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByFilters_ShouldRespectPagination() {
        // Given: Multiple animals matching filter
        Animal animal1 = Animal.builder().id(1L).name(PAGINATED_PREFIX + "1").build();
        Animal animal2 = Animal.builder().id(2L).name(PAGINATED_PREFIX + "2").build();

        AnimalDTO dto1 = AnimalDTO.builder().id(1L).name(PAGINATED_PREFIX + "1").build();
        AnimalDTO dto2 = AnimalDTO.builder().id(2L).name(PAGINATED_PREFIX + "2").build();

        // When: Searching with pagination returns only two out of a total of 3
        when(animalRepository.findByFilters(eq(PAGINATED_PREFIX), eq(null), eq(null), eq(null), eq(SMALL_PAGE)))
            .thenReturn(new PageImpl<>(Arrays.asList(animal1, animal2), SMALL_PAGE, 3));
        when(mapper.toDTO(animal1)).thenReturn(dto1);
        when(mapper.toDTO(animal2)).thenReturn(dto2);

        Page<AnimalDTO> result = animalService.findByFilters(
            PAGINATED_PREFIX,
            null,
            null,
            null,
            SMALL_PAGE
        );

        // Then: Should return paginated results
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();
        verify(animalRepository).findByFilters(eq(PAGINATED_PREFIX), eq(null), eq(null), eq(null), eq(SMALL_PAGE));
    }

    @Test
    void findByFilters_ShouldHandleInvalidDateRange() {
        // Given: Invalid date range (end before start)
        LocalDate startDate = TODAY;
        LocalDate endDate = TODAY.minusYears(1);

        // When: Searching with invalid date range
        when(animalRepository.findByFilters(eq(null), eq(startDate), eq(endDate), eq(null), eq(DEFAULT_PAGEABLE)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<AnimalDTO> result = animalService.findByFilters(
            null,
            startDate,
            endDate,
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Should return empty results
        assertThat(result.getContent()).isEmpty();
        verify(animalRepository).findByFilters(eq(null), eq(startDate), eq(endDate), eq(null), eq(DEFAULT_PAGEABLE));
    }

    @Test
    void update_ShouldThrowException_WhenVetDoesNotExist() {
        // Given: An existing animal and a non-existent vet ID
        final Animal existingAnimal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        final AnimalDTO updateDTO = AnimalDTO.builder()
            .id(ID)
            .name("Updated " + ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .vetIds(Collections.singleton(999L))
            .build();

        when(animalRepository.findById(ID)).thenReturn(Optional.of(existingAnimal));
        when(vetRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then: Updating with non-existent vet should throw exception
        assertThatThrownBy(() -> animalService.update(ID, updateDTO))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Vet not found with id: 999");
    }

    @Test
    void update_ShouldSucceed_WhenUpdatingWithEmptyVetSet() {
        // Given: An existing animal with vets
        final Animal existingAnimal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .vets(new HashSet<>(Collections.singletonList(
                Vet.builder().id(1L).name(VET_NAME).build()
            )))
            .build();

        final AnimalDTO updateDTO = AnimalDTO.builder()
            .id(ID)
            .name("Updated " + ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .vetIds(Collections.emptySet())
            .build();

        when(animalRepository.findById(ID)).thenReturn(Optional.of(existingAnimal));
        when(animalRepository.save(any(Animal.class))).thenReturn(existingAnimal);
        when(mapper.toDTO(any(Animal.class))).thenReturn(updateDTO);

        // When: Updating with empty vet set
        final AnimalDTO result = animalService.update(ID, updateDTO);

        // Then: The update should succeed and clear vets
        assertThat(result).isNotNull();
        assertThat(result.getVetIds()).isEmpty();
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void update_ShouldThrowException_WhenEmployeeDoesNotExist() {
        // Given: An existing animal and a non-existent employee ID
        final Animal existingAnimal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        final AnimalDTO updateDTO = AnimalDTO.builder()
            .id(ID)
            .name("Updated " + ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .responsibleEmployeeId(999L)
            .build();

        when(animalRepository.findById(ID)).thenReturn(Optional.of(existingAnimal));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then: Updating with non-existent employee should throw exception
        assertThatThrownBy(() -> animalService.update(ID, updateDTO))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Employee not found");
    }

    @Test
    void delete_ShouldSucceed_WhenAnimalExists() {
        // Given: An existing animal
        final Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        when(animalRepository.findById(ID)).thenReturn(Optional.of(animal));

        // When: Deleting the animal
        animalService.delete(ID);

        // Then: The delete should succeed
        verify(animalRepository).save(animal);
        verify(animalRepository).deleteById(ID);
    }

    @Test
    void findVetsByAnimalId_ShouldReturnEmptyPage_WhenNoVets() {
        // Given: An animal with no vets
        when(vetRepository.findByAnimalId(ID, DEFAULT_PAGEABLE))
            .thenReturn(Page.empty());

        // When: Finding vets for the animal
        final Page<VetDTO> result = animalService.findVetsByAnimalId(ID, DEFAULT_PAGEABLE);

        // Then: Should return empty page
        assertThat(result).isEmpty();
        verify(vetRepository).findByAnimalId(ID, DEFAULT_PAGEABLE);
    }

    @Test
    void findVetsByAnimalId_ShouldHandleMultiplePages() {
        // Given: An animal with multiple pages of vets
        final Vet vet1 = Vet.builder().id(1L).name("Dr. Smith").build();
        final Vet vet2 = Vet.builder().id(2L).name("Dr. Jones").build();
        final VetDTO dto1 = VetDTO.builder().id(1L).name("Dr. Smith").build();
        final VetDTO dto2 = VetDTO.builder().id(2L).name("Dr. Jones").build();

        when(vetRepository.findByAnimalId(ID, SMALL_PAGE))
            .thenReturn(new PageImpl<>(Arrays.asList(vet1, vet2), SMALL_PAGE, 4));
        when(mapper.toDTO(vet1)).thenReturn(dto1);
        when(mapper.toDTO(vet2)).thenReturn(dto2);

        // When: Finding vets with pagination
        final Page<VetDTO> result = animalService.findVetsByAnimalId(ID, SMALL_PAGE);

        // Then: Should return paginated results
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        verify(vetRepository).findByAnimalId(ID, SMALL_PAGE);
    }

    @Test
    void delete_ShouldSucceed_WhenAnimalHasAssociatedVets() {
        // Given: An existing animal with associated vets
        final Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .vets(new HashSet<>(Collections.singletonList(
                Vet.builder().id(1L).name(VET_NAME).build()
            )))
            .build();

        when(animalRepository.findById(ID)).thenReturn(Optional.of(animal));

        // When: Deleting the animal
        animalService.delete(ID);

        // Then: The animal should be saved with removed associations before being deleted
        verify(animalRepository).save(animal);
        verify(animalRepository).deleteById(ID);
        verify(vetRepository, never()).deleteById(any());
        assertThat(animal.getVets()).isEmpty();
    }
}