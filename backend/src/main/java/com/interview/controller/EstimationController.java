package com.interview.controller;

import com.interview.api.EstimationApi;
import com.interview.dto.estimation.EstimationDto;
import com.interview.service.EstimationService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/repair-orders/{repairOrderId}/estimation")
public class EstimationController implements EstimationApi {

    private final EstimationService estimationService;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<EstimationDto> submitEstimation(@PathVariable("repairOrderId") long repairOrderId) {
        var estimation = estimationService.submitEstimation(repairOrderId);
        return ResponseEntity.accepted().body(estimation);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<EstimationDto> getEstimation(@PathVariable("repairOrderId") long repairOrderId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(estimationService.getEstimation(repairOrderId));
    }
}
