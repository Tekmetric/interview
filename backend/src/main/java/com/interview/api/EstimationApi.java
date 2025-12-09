package com.interview.api;

import com.interview.dto.estimation.EstimationDto;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface EstimationApi {

    @Operation(summary = "Creates a new repair order", responses = {
            @ApiResponse(responseCode = "202", description = "The estimation submit was accepted"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Repair order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    }, security = @SecurityRequirement(name = "bearerAuth"),
            description = "Requires ROLE_STAFF or ROLE_ADMIN")
    ResponseEntity<EstimationDto> submitEstimation(long repairOrderId);

    @Operation(summary = "Gets the status of the submission", responses = {
            @ApiResponse(responseCode = "200", description = "Gets the estimation"),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Repair order not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    }, security = @SecurityRequirement(name = "bearerAuth"),
            description = "Requires ROLE_STAFF or ROLE_ADMIN")
    ResponseEntity<EstimationDto> getEstimation(long repairOrderId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
