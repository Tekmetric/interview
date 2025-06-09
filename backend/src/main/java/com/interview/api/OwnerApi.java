package com.interview.api;

import com.interview.dto.OwnerCreateRequestDTO;
import com.interview.dto.OwnerDTO;
import com.interview.dto.OwnerUpdateRequestDTO;
import com.interview.dto.PageResponseDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
      summary = "Get all owners (paginated)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of owners",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OwnerDTO.class)))
      })
  @GetMapping
  ResponseEntity<PageResponseDTO<OwnerDTO>> getOwners(
      @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")
          final int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") final int size);

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

  @Operation(
      summary = "Update an existing owner",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Owner updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OwnerDTO.class))),
        @ApiResponse(responseCode = "404", description = "Owner not found")
      })
  @PutMapping("/{id}")
  ResponseEntity<OwnerDTO> updateOwner(
      @Parameter(description = "ID of the owner") @PathVariable final Long id,
      @org.springframework.web.bind.annotation.RequestBody @NotNull @Valid
          final OwnerUpdateRequestDTO request);
}
