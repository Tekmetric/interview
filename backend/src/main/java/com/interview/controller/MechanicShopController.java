package com.interview.controller;

import com.interview.dto.request.MechanicShopCreationRequest;
import com.interview.dto.request.MechanicShopUpdateRequest;
import com.interview.dto.response.MechanicResponse;
import com.interview.dto.response.MechanicShopResponse;
import com.interview.facade.MechanicFacade;
import com.interview.facade.MechanicShopFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MechanicShopController {

    private final MechanicFacade mechanicFacade;
    private final MechanicShopFacade mechanicShopFacade;

    @GetMapping("/v1/mechanic-shop/{mechanicShopId}")
    public ResponseEntity<MechanicShopResponse> getMechanicShopWithMechanics(@PathVariable("mechanicShopId") Long mechanicShopId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mechanicShopFacade.getMechanicShopWithMechanics(mechanicShopId));
    }

    @GetMapping("/v1/mechanic-shop/{shopId}/mechanics")
    public ResponseEntity<List<MechanicResponse>> returnAllMechanicsFromShop(@PathVariable("shopId") Long shopId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mechanicFacade.getAllMechanics(shopId));
    }

    @PostMapping("/v1/mechanic-shop")
    public ResponseEntity<Long> createEmptyMechanicShop(@Valid @RequestBody MechanicShopCreationRequest mechanicShopCreationRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mechanicShopFacade.createEmptyMechanicShop(mechanicShopCreationRequest));
    }

    @PutMapping("/v1/mechanic-shop/{mechanicShopId}")
    public ResponseEntity<Long> updateMechanicShop(@PathVariable("mechanicShopId") Long mechanicShopId,
                                                   @Valid @RequestBody MechanicShopUpdateRequest mechanicShopUpdateRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mechanicShopFacade.updateMechanicShop(mechanicShopId, mechanicShopUpdateRequest));
    }

}