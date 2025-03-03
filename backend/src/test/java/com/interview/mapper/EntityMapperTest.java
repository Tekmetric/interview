package com.interview.mapper;

import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.CreateVetDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.dto.VetDTO;
import com.interview.model.Animal;
import com.interview.model.Employee;
import com.interview.model.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EntityMapperTest {

    @Autowired
    private EntityMapper mapper;

    // Given: Common test data
    private static final LocalDate TODAY = LocalDate.now();
    private static final String ANIMAL_NAME = "Max";
    private static final String ANIMAL_SPECIES = "Dog";
    private static final String ANIMAL_BREED = "Golden Retriever";
    private static final String EMPLOYEE_NAME = "John Doe";
    private static final String EMPLOYEE_JOB_TITLE = "Caretaker";
    private static final String EMPLOYEE_CONTACT = "john.doe@example.com";
    private static final String VET_NAME = "Dr. Smith";
    private static final String VET_SPECIALIZATION = "Surgery";
    private static final String VET_CONTACT = "dr.smith@example.com";
    private static final Long ID = 1L;

    @Test
    void toAnimalDTO_ShouldMapAllFields_WhenAnimalHasAllRelationships() {
        // Given: An animal with employee and vets
        final Employee employee = Employee.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        final Vet vet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        final Set<Vet> vets = new HashSet<>();
        vets.add(vet);

        final Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .responsibleEmployee(employee)
            .vets(vets)
            .build();

        // When: Converting to DTO
        final AnimalDTO dto = mapper.toDTO(animal);

        // Then: All fields should be correctly mapped
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getName()).isEqualTo(ANIMAL_NAME);
        assertThat(dto.getSpecies()).isEqualTo(ANIMAL_SPECIES);
        assertThat(dto.getBreed()).isEqualTo(ANIMAL_BREED);
        assertThat(dto.getDateOfBirth()).isEqualTo(TODAY);
        assertThat(dto.getResponsibleEmployeeId()).isEqualTo(ID);
        assertThat(dto.getVetIds()).containsExactly(ID);
    }

    @Test
    void toEmployeeDTO_ShouldMapAllFields_WhenEmployeeHasAnimals() {
        // Given: An employee with animals
        Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        Set<Animal> animals = new HashSet<>();
        animals.add(animal);

        Employee employee = Employee.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .animals(animals)
            .build();

        // When: Converting to DTO
        EmployeeDTO dto = mapper.toDTO(employee);

        // Then: All fields should be correctly mapped
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getName()).isEqualTo(EMPLOYEE_NAME);
        assertThat(dto.getJobTitle()).isEqualTo(EMPLOYEE_JOB_TITLE);
        assertThat(dto.getContactInformation()).isEqualTo(EMPLOYEE_CONTACT);
        assertThat(dto.getAnimalIds()).containsExactly(ID);
    }

    @Test
    void toVetDTO_ShouldMapAllFields_WhenVetHasAnimals() {
        // Given: A vet with animals
        Animal animal = Animal.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        Set<Animal> animals = new HashSet<>();
        animals.add(animal);

        Vet vet = Vet.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .animals(animals)
            .build();

        // When: Converting to DTO
        VetDTO dto = mapper.toDTO(vet);

        // Then: All fields should be correctly mapped
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getName()).isEqualTo(VET_NAME);
        assertThat(dto.getSpecialization()).isEqualTo(VET_SPECIALIZATION);
        assertThat(dto.getContactInformation()).isEqualTo(VET_CONTACT);
        assertThat(dto.getAnimalIds()).containsExactly(ID);
    }

    @Test
    void toAnimal_ShouldMapBasicFields_WhenConvertingFromDTO() {
        // Given: An animal DTO
        AnimalDTO dto = AnimalDTO.builder()
            .id(ID)
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        // When: Converting to entity
        Animal animal = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(animal).isNotNull();
        assertThat(animal.getId()).isEqualTo(ID);
        assertThat(animal.getName()).isEqualTo(ANIMAL_NAME);
        assertThat(animal.getSpecies()).isEqualTo(ANIMAL_SPECIES);
        assertThat(animal.getBreed()).isEqualTo(ANIMAL_BREED);
        assertThat(animal.getDateOfBirth()).isEqualTo(TODAY);
    }

    @Test
    void toEmployee_ShouldMapBasicFields_WhenConvertingFromDTO() {
        // Given: An employee DTO
        EmployeeDTO dto = EmployeeDTO.builder()
            .id(ID)
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        // When: Converting to entity
        Employee employee = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(ID);
        assertThat(employee.getName()).isEqualTo(EMPLOYEE_NAME);
        assertThat(employee.getJobTitle()).isEqualTo(EMPLOYEE_JOB_TITLE);
        assertThat(employee.getContactInformation()).isEqualTo(EMPLOYEE_CONTACT);
    }

    @Test
    void toVet_ShouldMapBasicFields_WhenConvertingFromDTO() {
        // Given: A vet DTO
        VetDTO dto = VetDTO.builder()
            .id(ID)
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        // When: Converting to entity
        Vet vet = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(vet).isNotNull();
        assertThat(vet.getId()).isEqualTo(ID);
        assertThat(vet.getName()).isEqualTo(VET_NAME);
        assertThat(vet.getSpecialization()).isEqualTo(VET_SPECIALIZATION);
        assertThat(vet.getContactInformation()).isEqualTo(VET_CONTACT);
    }

    @Test
    void toDTO_ShouldReturnNull_WhenEntityIsNull() {
        // When/Then: Converting null entities to DTOs
        assertThat(mapper.toDTO((Animal) null)).isNull();
        assertThat(mapper.toDTO((Employee) null)).isNull();
        assertThat(mapper.toDTO((Vet) null)).isNull();
    }

    @Test
    void toEntity_ShouldReturnNull_WhenDTOIsNull() {
        // When/Then: Converting null DTOs to entities
        assertThat(mapper.toEntity((AnimalDTO) null)).isNull();
        assertThat(mapper.toEntity((EmployeeDTO) null)).isNull();
        assertThat(mapper.toEntity((VetDTO) null)).isNull();
    }

    @Test
    void toAnimal_ShouldMapBasicFields_WhenConvertingFromCreateDTO() {
        // Given: A create animal DTO
        CreateAnimalDTO dto = CreateAnimalDTO.builder()
            .name(ANIMAL_NAME)
            .species(ANIMAL_SPECIES)
            .breed(ANIMAL_BREED)
            .dateOfBirth(TODAY)
            .build();

        // When: Converting to entity
        Animal animal = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(animal).isNotNull();
        assertThat(animal.getId()).isNull();
        assertThat(animal.getName()).isEqualTo(ANIMAL_NAME);
        assertThat(animal.getSpecies()).isEqualTo(ANIMAL_SPECIES);
        assertThat(animal.getBreed()).isEqualTo(ANIMAL_BREED);
        assertThat(animal.getDateOfBirth()).isEqualTo(TODAY);
    }

    @Test
    void toEmployee_ShouldMapBasicFields_WhenConvertingFromCreateDTO() {
        // Given: A create employee DTO
        CreateEmployeeDTO dto = CreateEmployeeDTO.builder()
            .name(EMPLOYEE_NAME)
            .jobTitle(EMPLOYEE_JOB_TITLE)
            .contactInformation(EMPLOYEE_CONTACT)
            .build();

        // When: Converting to entity
        Employee employee = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isNull();
        assertThat(employee.getName()).isEqualTo(EMPLOYEE_NAME);
        assertThat(employee.getJobTitle()).isEqualTo(EMPLOYEE_JOB_TITLE);
        assertThat(employee.getContactInformation()).isEqualTo(EMPLOYEE_CONTACT);
    }

    @Test
    void toVet_ShouldMapBasicFields_WhenConvertingFromCreateDTO() {
        // Given: A create vet DTO
        CreateVetDTO dto = CreateVetDTO.builder()
            .name(VET_NAME)
            .specialization(VET_SPECIALIZATION)
            .contactInformation(VET_CONTACT)
            .build();

        // When: Converting to entity
        Vet vet = mapper.toEntity(dto);

        // Then: Basic fields should be correctly mapped
        assertThat(vet).isNotNull();
        assertThat(vet.getId()).isNull();
        assertThat(vet.getName()).isEqualTo(VET_NAME);
        assertThat(vet.getSpecialization()).isEqualTo(VET_SPECIALIZATION);
        assertThat(vet.getContactInformation()).isEqualTo(VET_CONTACT);
    }
}