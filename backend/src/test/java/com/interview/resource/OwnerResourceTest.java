package com.interview.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.owner.OwnerCreateRequestDTO;
import com.interview.dto.owner.OwnerDTO;
import com.interview.dto.owner.OwnerUpdateRequestDTO;
import com.interview.dto.page.PageResponseDTO;
import com.interview.service.OwnerService;
import java.time.Instant;
import java.util.List;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Import(TestConfig.class)
@WebMvcTest(OwnerResource.class)
class OwnerResourceTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private OwnerService ownerService;

  @Autowired private ObjectMapper objectMapper;

  private static final EasyRandomParameters randomParams =
      new EasyRandomParameters()
          .randomize(
              field ->
                  field.getName().equals("personalNumber")
                      && field.getDeclaringClass().equals(OwnerCreateRequestDTO.class),
              () -> String.format("%06d", (int) (Math.random() * 1_000_000)))
          .randomize(
              field ->
                  field.getName().equals("personalNumber")
                      && field.getDeclaringClass().equals(OwnerUpdateRequestDTO.class),
              () -> String.format("%06d", (int) (Math.random() * 1_000_000)))
          .randomize(
              field ->
                  field.getName().equals("birthDate")
                      && (field.getDeclaringClass().equals(OwnerCreateRequestDTO.class)
                          || field.getDeclaringClass().equals(OwnerUpdateRequestDTO.class)),
              () -> Instant.now().minusSeconds(100));

  private static final EasyRandom easyRandom = new EasyRandom(randomParams);

  @Test
  void createOwner_returnsCreatedOwner() throws Exception {
    final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);
    final OwnerDTO response = easyRandom.nextObject(OwnerDTO.class);

    Mockito.when(ownerService.createOwner(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void getOwnerById_returnsOwner() throws Exception {
    final OwnerDTO response = easyRandom.nextObject(OwnerDTO.class);

    Mockito.when(ownerService.getOwnerById(eq(1L))).thenReturn(response);

    mockMvc
        .perform(get("/owners/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void deleteOwnerById_returnsDeletedOwner() throws Exception {
    final OwnerDTO response = easyRandom.nextObject(OwnerDTO.class);

    Mockito.when(ownerService.deleteOwnerById(eq(1L))).thenReturn(response);

    mockMvc
        .perform(delete("/owners/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void updateOwner_returnsUpdatedOwner() throws Exception {
    final OwnerUpdateRequestDTO request = easyRandom.nextObject(OwnerUpdateRequestDTO.class);
    final OwnerDTO response = easyRandom.nextObject(OwnerDTO.class);

    Mockito.when(ownerService.updateOwner(eq(1L), any())).thenReturn(response);

    mockMvc
        .perform(
            put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  void getOwners_returnsPagedOwners() throws Exception {
    final OwnerDTO owner1 = easyRandom.nextObject(OwnerDTO.class);
    final OwnerDTO owner2 = easyRandom.nextObject(OwnerDTO.class);
    final PageResponseDTO<OwnerDTO> page =
        PageResponseDTO.<OwnerDTO>builder()
            .content(List.of(owner1, owner2))
            .page(0)
            .size(2)
            .totalElements(2L)
            .totalPages(1)
            .last(false)
            .build();

    Mockito.when(ownerService.getOwners(any(String.class), any(PageRequest.class)))
        .thenReturn(page);

    mockMvc
        .perform(get("/owners").param("page", "0").param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  void createOwner_withInvalidPersonalNumber_returnsBadRequest() throws Exception {
    final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);
    request.setPersonalNumber("12A45B"); // Invalid: contains letters

    final ResultActions result =
        mockMvc.perform(
            post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors['Pattern.ownerCreateRequestDTO.personalNumber']").exists());
  }

  @Test
  void createOwner_withInvalidName_returnsBadRequest() throws Exception {
    final OwnerCreateRequestDTO request = easyRandom.nextObject(OwnerCreateRequestDTO.class);
    request.setName("John123"); // Invalid: contains digits

    final ResultActions result =
        mockMvc.perform(
            post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors['Pattern.ownerCreateRequestDTO.name']").exists());
  }
}
