package com.interview.resource;

import com.interview.dto.ErrorResponseDto;
import com.interview.dto.UpsertVehicleDto;
import com.interview.dto.VehicleDto;
import com.interview.dto.search.PageResponseDto;
import com.interview.dto.search.VehicleSearchDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public interface VehicleResourceDocumentation {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDto.class))}),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @Tag(name = "Get Vehicle by id")
    VehicleDto getById(final long id);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate resource",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @Tag(name = "Create Vehicle")
    VehicleDto createVehicle(@Valid @NotNull final UpsertVehicleDto upsertVehicleDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate resource",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @Tag(name = "Update Vehicle")
    VehicleDto updateVehicle(final long id, @Valid @NotNull final UpsertVehicleDto upsertVehicleDto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @Tag(name = "Delete Vehicle")
    void deleteVehicle(final long id);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid data",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @Tag(name = "Search Vehicles")
    PageResponseDto<VehicleDto> searchVehicles(@Valid @NotNull final VehicleSearchDto vehicleSearch);

}
