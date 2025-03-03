package com.interview.service;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.CreateVetDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.dto.VetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CachingIntegrationTest {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VetService vetService;

    @Autowired
    private CacheManager cacheManager;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String ANIMAL_NAME_ALICE = "Alice";
    private static final String ANIMAL_NAME_CHARLIE = "Charlie";
    private static final String ANIMAL_NAME_BOB = "Bob";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Mixed";
    private static final String EMPLOYEE_NAME_ALICE = "Alice Smith";
    private static final String EMPLOYEE_NAME_BOB = "Bob Jones";
    private static final String EMPLOYEE_NAME_CHARLIE = "Charlie Brown";
    private static final String EMPLOYEE_JOB = "Caretaker";
    private static final String EMPLOYEE_CONTACT = "employee@example.com";
    private static final String VET_NAME_ALICE = "Dr. Alice";
    private static final String VET_NAME_BOB = "Dr. Bob";
    private static final String VET_NAME_CHARLIE = "Dr. Charlie";
    private static final String VET_SPECIALIZATION = "General";
    private static final String VET_CONTACT = "vet@example.com";

    @SuppressWarnings("null")
    @BeforeEach
    void setUp() {
        // Clear all caches before each test
        cacheManager.getCacheNames()
            .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    // Animal Service Tests
    @Test
    void findAllAnimals_ShouldCacheResults_WithDifferentSortingAndPagination() {
        // Given: Multiple animals are created
        createTestAnimal(ANIMAL_NAME_ALICE);
        createTestAnimal(ANIMAL_NAME_BOB);
        createTestAnimal(ANIMAL_NAME_CHARLIE);

        // When: Fetching with different sort orders
        Pageable sortByNameAsc = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Pageable sortByNameDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        // Then: Results should be cached separately for different sort orders
        Page<AnimalDTO> resultAsc1 = animalService.findByFilters(null, null, null, null, sortByNameAsc);
        assertThat(resultAsc1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);

        Page<AnimalDTO> resultDesc1 = animalService.findByFilters(null, null, null, null, sortByNameDesc);
        assertThat(resultDesc1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_CHARLIE, ANIMAL_NAME_BOB, ANIMAL_NAME_ALICE);

        // When: Fetching again with the same parameters
        Page<AnimalDTO> resultAsc2 = animalService.findByFilters(null, null, null, null, sortByNameAsc);
        Page<AnimalDTO> resultDesc2 = animalService.findByFilters(null, null, null, null, sortByNameDesc);

        // Then: Results should be the same (cached)
        assertThat(resultAsc2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);
        assertThat(resultDesc2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_CHARLIE, ANIMAL_NAME_BOB, ANIMAL_NAME_ALICE);
    }

    @Test
    void findAllAnimals_ShouldCacheResults_WithDifferentPageSizes() {
        // Given: Multiple animals are created
        createTestAnimal(ANIMAL_NAME_ALICE);
        createTestAnimal(ANIMAL_NAME_BOB);
        createTestAnimal(ANIMAL_NAME_CHARLIE);

        // When: Fetching with different page sizes
        Pageable smallPage = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        Pageable largePage = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        // Then: Results should be cached separately for different page sizes
        Page<AnimalDTO> smallResult1 = animalService.findByFilters(null, null, null, null, smallPage);
        assertThat(smallResult1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB);

        Page<AnimalDTO> largeResult1 = animalService.findByFilters(null, null, null, null, largePage);
        assertThat(largeResult1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);

        // When: Fetching again with the same parameters
        Page<AnimalDTO> smallResult2 = animalService.findByFilters(null, null, null, null, smallPage);
        Page<AnimalDTO> largeResult2 = animalService.findByFilters(null, null, null, null, largePage);

        // Then: Results should be the same (cached)
        assertThat(smallResult2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB);
        assertThat(largeResult2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);
    }

    @Test
    void findAllAnimals_ShouldEvictCache_WhenEntityIsModified() {
        // Given: Multiple animals are created
        createTestAnimal(ANIMAL_NAME_ALICE);
        createTestAnimal(ANIMAL_NAME_BOB);
        AnimalDTO charlie = createTestAnimal(ANIMAL_NAME_CHARLIE);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        // When: Initial fetch
        Page<AnimalDTO> result1 = animalService.findByFilters(null, null, null, null, pageable);
        assertThat(result1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);

        // When: Modifying an entity
        charlie.setName("David");
        animalService.update(charlie.getId(), charlie);

        // Then: Cache should be evicted and new results returned
        Page<AnimalDTO> result2 = animalService.findByFilters(null, null, null, null, pageable);
        assertThat(result2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, "David");
    }

    @Test
    void findAllAnimals_ShouldEvictCache_WhenEntityIsDeleted() {
        // Given: Multiple animals are created
        createTestAnimal(ANIMAL_NAME_ALICE);
        createTestAnimal(ANIMAL_NAME_BOB);
        AnimalDTO charlie = createTestAnimal(ANIMAL_NAME_CHARLIE);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        // When: Initial fetch
        Page<AnimalDTO> result1 = animalService.findByFilters(null, null, null, null, pageable);
        assertThat(result1.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB, ANIMAL_NAME_CHARLIE);

        // When: Deleting an entity
        animalService.delete(charlie.getId());

        // Then: Cache should be evicted and new results returned
        Page<AnimalDTO> result2 = animalService.findByFilters(null, null, null, null, pageable);
        assertThat(result2.getContent())
            .extracting(AnimalDTO::getName)
            .containsExactly(ANIMAL_NAME_ALICE, ANIMAL_NAME_BOB);
    }

    // Employee Service Tests
    @Test
    void findAllEmployees_ShouldCacheResults_WithDifferentSortingAndPagination() {
        // Given: Multiple employees are created
        createTestEmployee(EMPLOYEE_NAME_ALICE);
        createTestEmployee(EMPLOYEE_NAME_BOB);
        createTestEmployee(EMPLOYEE_NAME_CHARLIE);

        // When: Fetching with different sort orders
        Pageable sortByNameAsc = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Pageable sortByNameDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        // Then: Results should be cached separately for different sort orders
        Page<EmployeeDTO> resultAsc1 = employeeService.findAll(sortByNameAsc);
        assertThat(resultAsc1.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_ALICE, EMPLOYEE_NAME_BOB, EMPLOYEE_NAME_CHARLIE);

        Page<EmployeeDTO> resultDesc1 = employeeService.findAll(sortByNameDesc);
        assertThat(resultDesc1.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_CHARLIE, EMPLOYEE_NAME_BOB, EMPLOYEE_NAME_ALICE);

        // When: Fetching again with the same parameters
        Page<EmployeeDTO> resultAsc2 = employeeService.findAll(sortByNameAsc);
        Page<EmployeeDTO> resultDesc2 = employeeService.findAll(sortByNameDesc);

        // Then: Results should be the same (cached)
        assertThat(resultAsc2.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_ALICE, EMPLOYEE_NAME_BOB, EMPLOYEE_NAME_CHARLIE);
        assertThat(resultDesc2.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_CHARLIE, EMPLOYEE_NAME_BOB, EMPLOYEE_NAME_ALICE);
    }

    @Test
    void findAllEmployees_ShouldEvictCache_WhenEntityIsModified() {
        // Given: Multiple employees are created
        createTestEmployee(EMPLOYEE_NAME_ALICE);
        createTestEmployee(EMPLOYEE_NAME_BOB);
        EmployeeDTO charlie = createTestEmployee(EMPLOYEE_NAME_CHARLIE);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        // When: Initial fetch
        Page<EmployeeDTO> result1 = employeeService.findAll(pageable);
        assertThat(result1.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_ALICE, EMPLOYEE_NAME_BOB, EMPLOYEE_NAME_CHARLIE);

        // When: Modifying an entity
        charlie.setName("David Smith");
        employeeService.update(charlie.getId(), charlie);

        // Then: Cache should be evicted and new results returned
        Page<EmployeeDTO> result2 = employeeService.findAll(pageable);
        assertThat(result2.getContent())
            .extracting(EmployeeDTO::getName)
            .containsExactly(EMPLOYEE_NAME_ALICE, EMPLOYEE_NAME_BOB, "David Smith");
    }

    // Vet Service Tests
    @Test
    void findAllVets_ShouldCacheResults_WithDifferentSortingAndPagination() {
        // Given: Multiple vets are created
        createTestVet(VET_NAME_ALICE);
        createTestVet(VET_NAME_BOB);
        createTestVet(VET_NAME_CHARLIE);

        // When: Fetching with different sort orders
        Pageable sortByNameAsc = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Pageable sortByNameDesc = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        // Then: Results should be cached separately for different sort orders
        Page<VetDTO> resultAsc1 = vetService.findAll(sortByNameAsc);
        assertThat(resultAsc1.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_ALICE, VET_NAME_BOB, VET_NAME_CHARLIE);

        Page<VetDTO> resultDesc1 = vetService.findAll(sortByNameDesc);
        assertThat(resultDesc1.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_CHARLIE, VET_NAME_BOB, VET_NAME_ALICE);

        // When: Fetching again with the same parameters
        Page<VetDTO> resultAsc2 = vetService.findAll(sortByNameAsc);
        Page<VetDTO> resultDesc2 = vetService.findAll(sortByNameDesc);

        // Then: Results should be the same (cached)
        assertThat(resultAsc2.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_ALICE, VET_NAME_BOB, VET_NAME_CHARLIE);
        assertThat(resultDesc2.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_CHARLIE, VET_NAME_BOB, VET_NAME_ALICE);
    }

    @Test
    void findAllVets_ShouldEvictCache_WhenEntityIsModified() {
        // Given: Multiple vets are created
        createTestVet(VET_NAME_ALICE);
        createTestVet(VET_NAME_BOB);
        VetDTO charlie = createTestVet(VET_NAME_CHARLIE);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        // When: Initial fetch
        Page<VetDTO> result1 = vetService.findAll(pageable);
        assertThat(result1.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_ALICE, VET_NAME_BOB, VET_NAME_CHARLIE);

        // When: Modifying an entity
        charlie.setName("Dr. David");
        vetService.update(charlie.getId(), charlie);

        // Then: Cache should be evicted and new results returned
        Page<VetDTO> result2 = vetService.findAll(pageable);
        assertThat(result2.getContent())
            .extracting(VetDTO::getName)
            .containsExactly(VET_NAME_ALICE, VET_NAME_BOB, "Dr. David");
    }

    // Helper methods
    private AnimalDTO createTestAnimal(String name) {
        CreateAnimalDTO createDTO = CreateAnimalDTO.builder()
            .name(name)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .build();
        return animalService.create(createDTO);
    }

    private EmployeeDTO createTestEmployee(String name) {
        CreateEmployeeDTO createDTO = CreateEmployeeDTO.builder()
            .name(name)
            .jobTitle(EMPLOYEE_JOB)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();
        return employeeService.create(createDTO);
    }

    private VetDTO createTestVet(String name) {
        CreateVetDTO createDTO = CreateVetDTO.builder()
            .name(name)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();
        return vetService.create(createDTO);
    }
}