package com.interview.api;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Owner", description = "CRUD operations for Owner")
@RequestMapping("/owners")
public interface OwnerApi {

  @Operation(
      summary = "Get owner by ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Owner found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OwnerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Owner not found")
      })
  @GetMapping("/{id}")
  ResponseEntity<OwnerDTO> getOwnerById(
      @Parameter(description = "ID of the owner") @PathVariable final Long id);

  @Operation(
      summary = "Create a new owner",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Owner created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OwnerDTO.class)))
      })
  @PostMapping
  ResponseEntity<OwnerDTO> createOwner(
      @RequestBody(
              description = "Owner to create",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = OwnerCreateRequestDTO.class)))
          @org.springframework.web.bind.annotation.RequestBody
          @NotNull
          @Valid
          final OwnerCreateRequestDTO owner);

  @Operation(
      summary = "Delete owner by ID",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Owner deleted",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OwnerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Owner not found")
      })
  @DeleteMapping("/{id}")
  ResponseEntity<OwnerDTO> deleteOwnerById(
      @Parameter(description = "ID of the owner") @PathVariable final Long id);
}
