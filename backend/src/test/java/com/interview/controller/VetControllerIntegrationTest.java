package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.AnimalDTO;
import com.interview.dto.CreateAnimalDTO;
import com.interview.dto.CreateVetDTO;
import com.interview.dto.VetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

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
public class VetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private VetDTO createTestVet(final String name) throws Exception {
        final CreateVetDTO createVetDTO = CreateVetDTO.builder()
            .name(name)
            .specialization("Surgery")
            .contactInformation("test@example.com")
            .build();

        final String response = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, VetDTO.class);
    }

    @Test
    void createVet_ShouldCreateAndReturnVet() throws Exception {
        // Given: A valid CreateVetDTO
        final CreateVetDTO createVetDTO = CreateVetDTO.builder()
            .name("Dr. Smith")
            .specialization("Surgery")
            .contactInformation("test@example.com")
            .build();

        // When/Then: Creating a new vet
        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Dr. Smith"))
            .andExpect(jsonPath("$.specialization").value("Surgery"))
            .andExpect(jsonPath("$.contactInformation").value("test@example.com"));
    }

    @Test
    void getVet_ShouldReturnVet() throws Exception {
        // Given: A created vet
        final VetDTO vetDTO = createTestVet("Dr. Jones");
        final String response = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetDTO)))
            .andReturn().getResponse().getContentAsString();
        final VetDTO createdVet = objectMapper.readValue(response, VetDTO.class);

        // When: Getting the vet by ID
        mockMvc.perform(get("/vets/{id}", createdVet.getId()))
            // Then: The vet should be returned
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdVet.getId()))
            .andExpect(jsonPath("$.name").value("Dr. Jones"))
            .andExpect(jsonPath("$.specialization").value("Surgery"))
            .andExpect(jsonPath("$.contactInformation").value("test@example.com"));
    }

    @Test
    void getAllVets_ShouldReturnAllVetsWithPagination() throws Exception {
        // Given: Multiple created vets
        createTestVet("Dr. Smith");
        createTestVet("Dr. Jones");

        // When: Getting all vets with pagination
        mockMvc.perform(get("/vets")
                .param("page", "0")
                .param("size", "10"))
            // Then: All vets should be returned with pagination metadata
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
            .andExpect(jsonPath("$.content[*].name", hasItems("Dr. Smith", "Dr. Jones")))
            .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.totalPages").exists())
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getAllVets_WithCustomPageSize_ShouldReturnCorrectNumberOfVets() throws Exception {
        // Given: Multiple created vets
        createTestVet("Dr. Smith");
        createTestVet("Dr. Jones");
        createTestVet("Dr. Wilson");

        // When: Getting vets with custom page size
        mockMvc.perform(get("/vets")
                .param("page", "0")
                .param("size", "2"))
            // Then: Only requested number of vets should be returned
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
            .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(2)));
    }

    @Test
    void updateVet_ShouldUpdateAndReturnVet() throws Exception {
        // Given: A created vet
        final VetDTO vetDTO = createTestVet("Dr. Smith");
        final String response = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetDTO)))
            .andReturn().getResponse().getContentAsString();
        final VetDTO createdVet = objectMapper.readValue(response, VetDTO.class);

        // When: Updating the vet
        final VetDTO updateDTO = createTestVet("Dr. Smith Updated");
        mockMvc.perform(put("/vets/{id}", createdVet.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            // Then: The vet should be updated and returned
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdVet.getId()))
            .andExpect(jsonPath("$.name").value("Dr. Smith Updated"))
            .andExpect(jsonPath("$.specialization").value("Surgery"))
            .andExpect(jsonPath("$.contactInformation").value("test@example.com"));
    }

    @Test
    void deleteVet_ShouldDeleteVet() throws Exception {
        // Given: A created vet
        final VetDTO vetDTO = createTestVet("Dr. Smith");
        final String response = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vetDTO)))
            .andReturn().getResponse().getContentAsString();
        final VetDTO createdVet = objectMapper.readValue(response, VetDTO.class);

        // When: Deleting the vet
        mockMvc.perform(delete("/vets/{id}", createdVet.getId()))
            // Then: The vet should be deleted
            .andExpect(status().isNoContent());

        // And: The vet should not be found
        mockMvc.perform(get("/vets/{id}", createdVet.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void createVet_WithInvalidData_ShouldReturn400() throws Exception {
        // Given: An invalid CreateVetDTO
        final CreateVetDTO invalidVet = new CreateVetDTO();
        invalidVet.setName(""); // Invalid: empty name
        invalidVet.setSpecialization(""); // Invalid: empty specialization
        invalidVet.setContactInformation("invalid-email"); // Invalid: wrong email format

        // When/Then: Creating with invalid data should fail
        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidVet)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.errors.name").exists())
            .andExpect(jsonPath("$.errors.specialization").exists())
            .andExpect(jsonPath("$.errors.contactInformation").exists());
    }

    @Test
    void getAllVets_WithSorting_ShouldReturnSortedVets() throws Exception {
        // Given
        createTestVet("Dr. Charlie");
        createTestVet("Dr. Alice");
        createTestVet("Dr. Bob");

        // When/Then: Sort by name ascending
        mockMvc.perform(get("/vets")
                .param("sortBy", "name")
                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Dr. Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Dr. Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Dr. Charlie"));

        // When/Then: Sort by name descending
        mockMvc.perform(get("/vets")
                .param("sortBy", "name")
                .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name").value("Dr. Charlie"))
                .andExpect(jsonPath("$.content[1].name").value("Dr. Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Dr. Alice"));
    }

    @Test
    void deleteVet_ShouldOnlyRemoveVetAndJoinTableEntries_WhenVetHasAnimals() throws Exception {
        // Given: Create a vet and an animal
        final CreateVetDTO createVetDTO = CreateVetDTO.builder()
            .name("Dr. Smith")
            .specialization("Surgery")
            .contactInformation("dr.smith@example.com")
            .build();
        
        final String vetResponse = mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVetDTO)))
            .andReturn().getResponse().getContentAsString();
        final VetDTO savedVet = objectMapper.readValue(vetResponse, VetDTO.class);

        // Create an animal
        final CreateAnimalDTO createAnimalDTO = CreateAnimalDTO.builder()
            .name("Max")
            .dateOfBirth(LocalDate.now().minusYears(2))
            .species("Dog")
            .breed("Mixed")
            .build();
        
        final String animalResponse = mockMvc.perform(post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAnimalDTO)))
            .andReturn().getResponse().getContentAsString();
        final AnimalDTO savedAnimal = objectMapper.readValue(animalResponse, AnimalDTO.class);

        // Update animal to associate it with the vet
        savedAnimal.setVetIds(Collections.singleton(savedVet.getId()));
        mockMvc.perform(put("/animals/{id}", savedAnimal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedAnimal)))
                .andExpect(status().isOk());

        // When: Delete the vet
        mockMvc.perform(delete("/vets/{id}", savedVet.getId()))
                .andExpect(status().isNoContent());

        // Then: Vet should be deleted but animal should still exist
        mockMvc.perform(get("/vets/{id}", savedVet.getId()))
                .andExpect(status().isNotFound());
        
        mockMvc.perform(get("/animals/{id}", savedAnimal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.vetIds").isEmpty());
    }
}