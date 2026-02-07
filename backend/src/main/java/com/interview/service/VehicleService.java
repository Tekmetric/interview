package com.interview.service;

import com.interview.model.dto.VehiclePageResponse;
import com.interview.model.dto.VehicleRequest;
import com.interview.model.dto.VehicleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    VehicleResponse getVehicleById(Long id);

    List<VehicleResponse> getAllVehicles();

    VehiclePageResponse getAllVehicles(Pageable pageable);

    VehicleResponse updateVehicle(Long id, VehicleRequest request);

    void deleteVehicle(Long id);
}