package com.interview.service;

import com.interview.model.dto.VehicleRequest;
import com.interview.model.dto.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    VehicleResponse getVehicleById(Long id);

    List<VehicleResponse> getAllVehicles();

    VehicleResponse updateVehicle(Long id, VehicleRequest request);

    void deleteVehicle(Long id);
}