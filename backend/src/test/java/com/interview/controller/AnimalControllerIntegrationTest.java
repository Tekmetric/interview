package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.CreateEmployeeDTO;
import com.interview.dto.CreateVetDTO;
import com.interview.dto.EmployeeDTO;
import com.interview.dto.VetDTO;
import com.interview.service.AnimalService;
import com.interview.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnimalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private EmployeeService employeeService;

    // Given
    private AnimalDTO createTestAnimal(final String name, final LocalDate dateOfBirth) {
        final CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
            .name(name)
            .dateOfBirth(dateOfBirth)
            .species("Dog")
            .breed("Mixed")
            .build();
        return animalService.create(createAnimalDTO);
    }

    @Test
    void createAnimal_ShouldCreateAndReturnAnimal() throws Exception {
        // Given
        final CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
            .name("Max")
            .dateOfBirth(LocalDate.now().minusYears(2))
            .species("Dog")
            .breed("Labrador")
            .build();

        // When/Then
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAnimalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.breed").value("Labrador"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.responsibleEmployeeId").doesNotExist())
                .andExpect(jsonPath("$.vetIds").doesNotExist());
    }

    @Test
    void getAnimal_ShouldReturnAnimal() throws Exception {
        // Given
        AnimalDTO savedAnimal = createTestAnimal("Buddy", LocalDate.now().minusYears(3));

        // When/Then
        mockMvc.perform(get("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.breed").value("Mixed"))
                .andExpect(jsonPath("$.id").value(savedAnimal.getId()));
    }

    @Test
    void getAllAnimals_WithNameFilter_ShouldReturnMatchingAnimals() throws Exception {
        // Given
        createTestAnimal("Max", LocalDate.now().minusYears(2));
        createTestAnimal("Maximus", LocalDate.now().minusYears(3));
        createTestAnimal("Buddy", LocalDate.now().minusYears(1));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("name", "Max"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Max", "Maximus")));
    }

    @Test
    void getAllAnimals_WithAgeFilter_ShouldReturnMatchingAnimals() throws Exception {
        // Given
        createTestAnimal("Young", LocalDate.now().minusYears(1));
        createTestAnimal("Middle", LocalDate.now().minusYears(5));
        createTestAnimal("Old", LocalDate.now().minusYears(10));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("minAge", "2")
                .param("maxAge", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Middle"));
    }

    @Test
    void updateAnimal_ShouldUpdateAndReturnAnimal() throws Exception {
        // Given
        final AnimalDTO savedAnimal = createTestAnimal("Original", LocalDate.now().minusYears(2));
        savedAnimal.setName("Updated");

        // When/Then
        mockMvc.perform(put("/animals/{id}", savedAnimal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedAnimal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.breed").value("Mixed"))
                .andExpect(jsonPath("$.id").value(savedAnimal.getId()));
    }

    @Test
    void deleteAnimal_ShouldDeleteAnimal() throws Exception {
        // Given
        final AnimalDTO savedAnimal = createTestAnimal("ToDelete", LocalDate.now().minusYears(2));

        // When
        mockMvc.perform(delete("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isNoContent());

        // Then
        // Add a small delay to ensure the delete operation is completed
        Thread.sleep(100);
        mockMvc.perform(get("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAnimalVets_ShouldReturnVets() throws Exception {
        // Given
        final AnimalDTO savedAnimal = createTestAnimal("WithVets", LocalDate.now().minusYears(2));

        // When/Then
        mockMvc.perform(get("/animals/{id}/vets", savedAnimal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.number").exists());
    }

    @Test
    void createAnimal_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        CreateAnimalDTO invalidAnimal = new CreateAnimalDTO();
        invalidAnimal.setName(""); // Invalid: empty name
        invalidAnimal.setSpecies(""); // Invalid: empty species
        invalidAnimal.setBreed(""); // Invalid: empty breed
        invalidAnimal.setDateOfBirth(LocalDate.now().plusDays(1)); // Invalid: future date

        // When
        mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAnimal)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.species").exists())
                .andExpect(jsonPath("$.errors.breed").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());
    }

    @Test
    void getAllAnimals_WithEmployeeId_ShouldReturnMatchingAnimals() throws Exception {
        // Given
        // Create an employee first
        CreateEmployeeDTO createEmployeeDTO = CreateEmployeeDTO.builder()
            .name("John Doe")
            .jobTitle("Veterinary Assistant")
            .contactInformation("john@example.com")
            .build();
        EmployeeDTO employee = employeeService.create(createEmployeeDTO);

        CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
            .name("Max")
            .dateOfBirth(LocalDate.now().minusYears(2))
            .species("Dog")
            .breed("Labrador")
            .build();
        AnimalDTO createdAnimal = animalService.create(createAnimalDTO);
        
        // Update the animal with the created employee's ID
        createdAnimal.setResponsibleEmployeeId(employee.getId());
        animalService.update(createdAnimal.getId(), createdAnimal);

        // When/Then
        mockMvc.perform(get("/animals")
                .param("employeeId", employee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Max"))
                .andExpect(jsonPath("$.content[0].responsibleEmployeeId").value(employee.getId()));
    }

    @Test
    void getAllAnimals_WithEmptyName_ShouldReturnAllAnimals() throws Exception {
        // Given
        createTestAnimal("Max", LocalDate.now().minusYears(2));
        createTestAnimal("Buddy", LocalDate.now().minusYears(3));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("name", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void getAllAnimals_WithOnlyMinAge_ShouldReturnMatchingAnimals() throws Exception {
        // Given
        createTestAnimal("Young", LocalDate.now().minusYears(1));
        createTestAnimal("Middle", LocalDate.now().minusYears(5));
        createTestAnimal("Old", LocalDate.now().minusYears(10));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("minAge", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Middle", "Old")));
    }

    @Test
    void getAllAnimals_WithOnlyMaxAge_ShouldReturnMatchingAnimals() throws Exception {
        // Given
        createTestAnimal("Young", LocalDate.now().minusYears(1));
        createTestAnimal("Middle", LocalDate.now().minusYears(5));
        createTestAnimal("Old", LocalDate.now().minusYears(10));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("maxAge", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Young"));
    }

    @Test
    void getAllAnimals_WithNoFilters_ShouldReturnAllAnimals() throws Exception {
        // Given
        createTestAnimal("First", LocalDate.now().minusYears(1));
        createTestAnimal("Second", LocalDate.now().minusYears(2));

        // When/Then
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("First", "Second")));
    }

    @Test
    void getAllAnimals_ShouldReturnAllAnimalsWithPagination() throws Exception {
        // Given
        createTestAnimal("Max", LocalDate.now().minusYears(2));
        createTestAnimal("Buddy", LocalDate.now().minusYears(3));
        createTestAnimal("Charlie", LocalDate.now().minusYears(1));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.content[*].name", hasItems("Max", "Buddy", "Charlie")))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getAllAnimals_WithCustomPageSize_ShouldReturnCorrectNumberOfAnimals() throws Exception {
        // Given
        createTestAnimal("Max", LocalDate.now().minusYears(2));
        createTestAnimal("Buddy", LocalDate.now().minusYears(3));
        createTestAnimal("Charlie", LocalDate.now().minusYears(1));

        // When/Then
        mockMvc.perform(get("/animals")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(2)));
    }

    @Test
    void getAllAnimals_WithSorting_ShouldReturnSortedAnimals() throws Exception {
        // Given
        createTestAnimal("Charlie", LocalDate.now().minusYears(2));
        createTestAnimal("Alice", LocalDate.now().minusYears(3));
        createTestAnimal("Bob", LocalDate.now().minusYears(1));

        // When/Then: Sort by name ascending
        mockMvc.perform(get("/animals")
                .param("sortBy", "name")
                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Charlie"));

        // When/Then: Sort by name descending
        mockMvc.perform(get("/animals")
                .param("sortBy", "name")
                .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Charlie"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Alice"));

        // When/Then: Sort by dateOfBirth ascending (age descending)
        mockMvc.perform(get("/animals")
                .param("sortBy", "dateOfBirth")
                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Charlie"))
                .andExpect(jsonPath("$.content[2].name").value("Bob"));
    }

    @Test
    void deleteAnimal_ShouldOnlyRemoveAnimalAndJoinTableEntries_WhenAnimalHasVets() throws Exception {
        // Given: Create a vet
        CreateVetDTO createVetDTO = CreateVetDTO.builder()
                .name("Dr. Smith")
                .specialization("Surgery")
                .contactInformation("drsmith@example.com")
                .build();
        
        MvcResult vetResult = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        
        VetDTO savedVet = objectMapper.readValue(vetResult.getResponse().getContentAsString(), VetDTO.class);

        // And: Create an animal
        CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
                .name("Max")
                .species("Dog")
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .build();

        MvcResult animalResult = mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAnimalDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        AnimalDTO savedAnimal = objectMapper.readValue(animalResult.getResponse().getContentAsString(), AnimalDTO.class);

        // And: Associate the animal with the vet
        savedAnimal.setVetIds(Collections.singleton(savedVet.getId()));
        mockMvc.perform(put("/animals/{id}", savedAnimal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedAnimal)))
                .andExpect(status().isOk());

        // When: Deleting the animal
        mockMvc.perform(delete("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isNoContent());

        // Then: The animal should be deleted
        mockMvc.perform(get("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isNotFound());

        // And: The vet should still exist but have no animals
        MvcResult vetAfterDeleteResult = mockMvc.perform(get("/vets/{id}", savedVet.getId()))
                .andExpect(status().isOk())
                .andReturn();

        VetDTO vetAfterDelete = objectMapper.readValue(vetAfterDeleteResult.getResponse().getContentAsString(), VetDTO.class);
        assertThat(vetAfterDelete.getAnimalIds()).isEmpty();
    }
}