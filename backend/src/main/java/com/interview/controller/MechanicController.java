package com.interview.controller;

import com.interview.dto.request.MechanicCreationRequest;
import com.interview.dto.request.MechanicUpdateRequest;
import com.interview.dto.response.MechanicResponse;
import com.interview.facade.MechanicFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MechanicController {

    private final MechanicFacade mechanicFacade;

    @GetMapping("/v1/mechanic/{mechanicId}")
    public ResponseEntity<MechanicResponse> returnMechanicById(@PathVariable("mechanicId") Long mechanicId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mechanicFacade.getMechanic(mechanicId));
    }

    @PostMapping("/v1/mechanic")
    public ResponseEntity<Long> createMechanic(@Valid @RequestBody MechanicCreationRequest mechanicCreationRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mechanicFacade.createMechanic(mechanicCreationRequest));
    }

    @PutMapping("/v1/mechanic/{mechanicId}")
    public ResponseEntity<Long> updateMechanic(@PathVariable("mechanicId") Long mechanicId,
                                               @Valid @RequestBody MechanicUpdateRequest mechanicUpdateRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mechanicFacade.updateMechanic(mechanicId, mechanicUpdateRequest));
    }

    @DeleteMapping("/v1/mechanic/{mechanicId}")
    public ResponseEntity<Void> deleteMechanic(@PathVariable Long mechanicId) {
        mechanicFacade.deleteMechanic(mechanicId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
