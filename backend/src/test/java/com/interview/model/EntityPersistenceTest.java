package com.interview.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String EMPLOYEE_NAME = "John Doe";
    private static final String EMPLOYEE_JOB_TITLE = "Animal Caretaker";
    private static final String EMPLOYEE_CONTACT = "john.doe@example.com";
    private static final String VET_NAME = "Dr. Smith";
    private static final String VET_SPECIALIZATION = "General Practice";
    private static final String VET_CONTACT = "dr.smith@example.com";
    private static final String ANIMAL_NAME = "Max";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Golden Retriever";

    @Test
    public void shouldPersistAnimalWithEmployeeAndVet() {
        // Given: An employee and a vet are persisted in the database
        Employee employee = Employee.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();
        entityManager.persistAndFlush(employee);
        assertThat(employee.getId()).isNotNull();

        Vet vet = Vet.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();
        entityManager.persistAndFlush(vet);
        assertThat(vet.getId()).isNotNull();

        Animal animal = Animal.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY.minusYears(2))
            .responsibleEmployee(employee)
            .build();
        animal.getVets().add(vet);

        // When: The animal is persisted and then retrieved from the database
        entityManager.persistAndFlush(animal);
        entityManager.clear(); // Clear persistence context to force reload from DB
        Animal foundAnimal = entityManager.find(Animal.class, animal.getId());

        // Then: The animal and its relationships are correctly persisted
        assertThat(foundAnimal).isNotNull();
        assertThat(foundAnimal.getId()).isNotNull();
        assertThat(foundAnimal.getName()).isEqualTo(ANIMAL_NAME);
        assertThat(foundAnimal.getSpecies()).isEqualTo(ANIMAL_SPECIES);
        assertThat(foundAnimal.getBreed()).isEqualTo(ANIMAL_BREED);
        
        // Then: The employee relationship is correctly persisted
        Employee foundEmployee = foundAnimal.getResponsibleEmployee();
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getId()).isEqualTo(employee.getId());
        assertThat(foundEmployee.getName()).isEqualTo(EMPLOYEE_NAME);
        
        // Then: The vet relationship is correctly persisted
        assertThat(foundAnimal.getVets())
            .hasSize(1)
            .extracting("name")
            .containsExactly(VET_NAME);
    }
}