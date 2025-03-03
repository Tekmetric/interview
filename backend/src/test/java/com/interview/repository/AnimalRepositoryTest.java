package com.interview.repository;

import com.interview.model.Animal;
import com.interview.model.Employee;
import com.interview.model.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AnimalJpaRepository.class})
class AnimalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnimalRepository animalRepository;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String ANIMAL_NAME = "Max";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Golden Retriever";
    private static final String SECOND_ANIMAL_NAME = "Maximus";
    private static final String THIRD_ANIMAL_NAME = "Rex";
    private static final String EMPLOYEE_NAME = "John Doe";
    private static final String EMPLOYEE_JOB_TITLE = "Caretaker";
    private static final String EMPLOYEE_CONTACT = "john.doe@example.com";
    private static final String UPPERCASE_NAME = "MAX";
    private static final String LOWERCASE_NAME = "max";
    private static final String START_DATE_NAME = "Start";
    private static final String END_DATE_NAME = "End";
    private static final String NON_MATCHING_NAME = "Fluffy";
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);

    @Test
    void findByFilters_ShouldReturnAnimals_WhenAllFiltersProvided() {
        // Given: An employee and multiple animals with different attributes
        final Employee employee = Employee.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .animals(new HashSet<>())
            .build();
        entityManager.persist(employee);

        final Animal animal1 = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .responsibleEmployee(employee)
            .vets(new HashSet<>())
            .build();

        final Animal animal2 = Animal.builder()
            .name(SECOND_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(1))
            .responsibleEmployee(employee)
            .vets(new HashSet<>())
            .build();

        final Animal animal3 = Animal.builder()
            .name(THIRD_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(3))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(animal1);
        entityManager.persist(animal2);
        entityManager.persist(animal3);
        entityManager.flush();

        // When: Searching with all filters
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            "max",
            TODAY.minusYears(3),
            TODAY,
            employee.getId(),
            DEFAULT_PAGEABLE
        );

        // Then: Only animals matching all criteria should be found
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(ANIMAL_NAME, SECOND_ANIMAL_NAME);
        assertThat(foundAnimals.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_ShouldReturnAnimals_WhenOnlyNameProvided() {
        // Given: Multiple animals with different names
        final Animal animal1 = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();

        final Animal animal2 = Animal.builder()
            .name(SECOND_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(1))
            .vets(new HashSet<>())
            .build();

        final Animal animal3 = Animal.builder()
            .name(THIRD_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(3))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(animal1);
        entityManager.persist(animal2);
        entityManager.persist(animal3);
        entityManager.flush();

        // When: Searching with only name filter
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            "max",
            null,
            null,
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Only animals with matching names should be found
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(ANIMAL_NAME, SECOND_ANIMAL_NAME);
        assertThat(foundAnimals.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_ShouldReturnAnimals_WhenOnlyDateRangeProvided() {
        // Given: Multiple animals with different birth dates
        final Animal animal1 = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();

        final Animal animal2 = Animal.builder()
            .name(SECOND_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(1))
            .vets(new HashSet<>())
            .build();

        final Animal animal3 = Animal.builder()
            .name(THIRD_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(3))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(animal1);
        entityManager.persist(animal2);
        entityManager.persist(animal3);
        entityManager.flush();

        // When: Searching with only date range filter
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            null,
            TODAY.minusYears(2),
            TODAY.minusYears(1),
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Only animals within date range should be found
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(ANIMAL_NAME, SECOND_ANIMAL_NAME);
        assertThat(foundAnimals.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_ShouldReturnAnimals_WhenOnlyEmployeeProvided() {
        // Given: Multiple animals with different employees
        final Employee employee = Employee.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .animals(new HashSet<>())
            .build();
        entityManager.persist(employee);

        final Animal animal1 = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .responsibleEmployee(employee)
            .vets(new HashSet<>())
            .build();

        final Animal animal2 = Animal.builder()
            .name(SECOND_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(1))
            .responsibleEmployee(employee)
            .vets(new HashSet<>())
            .build();

        final Animal animal3 = Animal.builder()
            .name(THIRD_ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(3))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(animal1);
        entityManager.persist(animal2);
        entityManager.persist(animal3);
        entityManager.flush();

        // When: Searching with only employee filter
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            null,
            null,
            null,
            employee.getId(),
            DEFAULT_PAGEABLE
        );

        // Then: Only animals assigned to the employee should be found
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(ANIMAL_NAME, SECOND_ANIMAL_NAME);
        assertThat(foundAnimals.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_ShouldReturnEmptyPage_WhenNoMatchesFound() {
        // Given: Animals that don't match any filter
        final Animal animal = Animal.builder()
            .name(NON_MATCHING_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(5))
            .vets(new HashSet<>())
            .build();
        entityManager.persist(animal);
        entityManager.flush();

        // When: Searching with non-matching filters
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            ANIMAL_NAME,
            TODAY.minusYears(2),
            TODAY.minusYears(1),
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Should return empty page
        assertThat(foundAnimals.getContent()).isEmpty();
        assertThat(foundAnimals.getTotalElements()).isZero();
    }

    @Test
    void findByFilters_ShouldBeCaseInsensitive_WhenSearchingByName() {
        // Given: Animals with different case names
        final Animal animal1 = Animal.builder()
            .name(UPPERCASE_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusMonths(1))
            .vets(new HashSet<>())
            .build();

        final Animal animal2 = Animal.builder()
            .name(LOWERCASE_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusDays(16))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(animal1);
        entityManager.persist(animal2);
        entityManager.flush();

        // When: Searching with lowercase name
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            LOWERCASE_NAME,
            null,
            null,
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Should find both animals regardless of case
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(UPPERCASE_NAME, LOWERCASE_NAME);
    }

    @Test
    void findByFilters_ShouldIncludeBoundaryDates_WhenSearchingByDateRange() {
        // Given: Animals with boundary dates
        final Animal exactStartDate = Animal.builder()
            .name(START_DATE_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();

        final Animal exactEndDate = Animal.builder()
            .name(END_DATE_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(1))
            .vets(new HashSet<>())
            .build();

        entityManager.persist(exactStartDate);
        entityManager.persist(exactEndDate);
        entityManager.flush();

        // When: Searching with exact boundary dates
        final Page<Animal> foundAnimals = animalRepository.findByFilters(
            null,
            TODAY.minusYears(2),
            TODAY.minusYears(1),
            null,
            DEFAULT_PAGEABLE
        );

        // Then: Should include animals with exact boundary dates
        assertThat(foundAnimals.getContent()).hasSize(2)
            .extracting(Animal::getName)
            .containsExactlyInAnyOrder(START_DATE_NAME, END_DATE_NAME);
    }

    @Test
    void delete_ShouldOnlyRemoveAnimalAndJoinTableEntries_WhenAnimalHasVets() {
        // Given: A vet and an animal with a bidirectional relationship
        final Vet vet = Vet.builder()
            .name("Dr. Smith")
            .specialization("Surgery")
            .contactInformation("dr.smith@example.com")
            .animals(new HashSet<>())
            .build();
        entityManager.persist(vet);

        final Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();
        animal.addVet(vet);
        entityManager.persist(animal);
        entityManager.flush();

        final Long animalId = animal.getId();
        final Long vetId = vet.getId();

        // When: Deleting the animal
        animalRepository.deleteById(animalId);
        entityManager.flush();
        entityManager.clear();

        // Then: The animal should be deleted but the vet should still exist
        assertThat(animalRepository.findById(animalId)).isEmpty();
        final Vet savedVet = entityManager.find(Vet.class, vetId);
        assertThat(savedVet).isNotNull();
        assertThat(savedVet.getAnimals()).isEmpty();
    }

    @Test
    void save_ShouldMaintainBidirectionalRelationship_WhenAddingVetToAnimal() {
        // Given
        Vet vet = Vet.builder()
                .name("Dr. Smith")
                .specialization("Surgery")
                .contactInformation("drsmith@example.com")
                .build();
        entityManager.persist(vet);

        Animal animal = Animal.builder()
                .name("Max")
                .species("Dog")
                .breed("Labrador")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .build();
        entityManager.persist(animal);

        // When
        animal.addVet(vet);
        animalRepository.save(animal);
        entityManager.flush();
        entityManager.clear();

        // Then
        Animal savedAnimal = animalRepository.findById(animal.getId()).orElseThrow();
        Vet savedVet = entityManager.find(Vet.class, vet.getId());
        
        assertThat(savedAnimal.getVets())
            .hasSize(1)
            .extracting("id")
            .containsExactly(vet.getId());
        
        assertThat(savedVet.getAnimals())
            .hasSize(1)
            .extracting("id")
            .containsExactly(animal.getId());
    }
}