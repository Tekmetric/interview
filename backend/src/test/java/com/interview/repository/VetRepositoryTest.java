package com.interview.repository;

import com.interview.model.Animal;
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
@Import(VetJpaRepository.class)
class VetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VetRepository vetRepository;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String VET_NAME = "Dr. Smith";
    private static final String VET_SPECIALIZATION = "Surgery";
    private static final String VET_CONTACT = "dr.smith@example.com";
    private static final String SECOND_VET_NAME = "Dr. Jones";
    private static final String SECOND_VET_SPECIALIZATION = "Cardiology";
    private static final String SECOND_VET_CONTACT = "dr.jones@example.com";
    private static final String ANIMAL_NAME = "Max";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Golden Retriever";
    private static final Long NON_EXISTENT_ID = 999L;
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);

    @Test
    void findByAnimalId_ShouldReturnMultipleVets_WhenAnimalHasMultipleVets() {
        // Given: Multiple vets and an animal are persisted with relationships
        final Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();

        final Vet vet1 = Vet.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .animals(new HashSet<>())
            .build();
        final Vet vet2 = Vet.builder()
            .name(SECOND_VET_NAME)
            .specialization(SECOND_VET_SPECIALIZATION)
            .contactInformation(SECOND_VET_CONTACT)
            .animals(new HashSet<>())
            .build();

        // Persist so they all get IDs assigned
        entityManager.persist(animal);
        entityManager.persist(vet1);
        entityManager.persist(vet2);
        entityManager.flush();

        // Set up bidirectional relationship
        animal.getVets().add(vet1);
        animal.getVets().add(vet2);
        vet1.getAnimals().add(animal);
        vet2.getAnimals().add(animal);

        // Persist again to update relationships
        entityManager.persist(animal);
        entityManager.persist(vet1);
        entityManager.persist(vet2);
        entityManager.flush();

        // When: Searching for vets by animal ID
        final Page<Vet> foundVets = vetRepository.findByAnimalId(animal.getId(), DEFAULT_PAGEABLE);

        // Then: Both assigned vets should be found
        assertThat(foundVets.getContent()).hasSize(2)
            .extracting(Vet::getName)
            .containsExactlyInAnyOrder(VET_NAME, SECOND_VET_NAME);
        assertThat(foundVets.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByAnimalId_ShouldReturnEmptyPage_WhenAnimalHasNoVets() {
        // Given: An animal is persisted without any vets
        final Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();
        entityManager.persist(animal);
        entityManager.flush();

        // When: Searching for vets by animal ID
        final Page<Vet> foundVets = vetRepository.findByAnimalId(animal.getId(), DEFAULT_PAGEABLE);

        // Then: No vets should be found
        assertThat(foundVets.getContent()).isEmpty();
        assertThat(foundVets.getTotalElements()).isZero();
    }

    @Test
    void findByAnimalId_ShouldReturnEmptyPage_WhenAnimalDoesNotExist() {
        // When: Searching for vets by non-existent animal ID
        final Page<Vet> foundVets = vetRepository.findByAnimalId(NON_EXISTENT_ID, DEFAULT_PAGEABLE);

        // Then: No vets should be found
        assertThat(foundVets.getContent()).isEmpty();
        assertThat(foundVets.getTotalElements()).isZero();
    }

    @Test
    void delete_ShouldOnlyRemoveVetAndJoinTableEntries_WhenVetHasAnimals() {
        // Given: A vet and an animal with a bidirectional relationship
        final Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .vets(new HashSet<>())
            .build();

        final Vet vet = Vet.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .animals(new HashSet<>())
            .build();

        // Persist entities and establish relationship
        entityManager.persist(animal);
        entityManager.persist(vet);
        vet.addAnimal(animal);
        entityManager.flush();

        final Long animalId = animal.getId();
        final Long vetId = vet.getId();

        // When: Removing animal associations and deleting the vet
        vet.removeAllAnimals();
        entityManager.persist(vet);
        entityManager.flush();
        vetRepository.deleteById(vetId);
        entityManager.flush();
        entityManager.clear();

        // Then: The vet should be deleted but the animal should still exist
        assertThat(vetRepository.findById(vetId)).isEmpty();
        
        final Animal foundAnimal = entityManager.find(Animal.class, animalId);
        assertThat(foundAnimal).isNotNull();
        assertThat(foundAnimal.getVets()).isEmpty();
    }
}