package com.interview.service;

import com.interview.exception.ValidationException;
import com.interview.model.VehicleRecall;
import com.interview.repository.VehicleRecallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleRecallService {
    private final VehicleRecallRepository vehicleRecallRepository;

    @Autowired
    public VehicleRecallService(VehicleRecallRepository vehicleRecallRepository) {
        this.vehicleRecallRepository = vehicleRecallRepository;
    }

    public List<VehicleRecall> getAllRecalls() {
        return vehicleRecallRepository.findAll();
    }

    public Optional<VehicleRecall> getRecallById(Long id) {
        return vehicleRecallRepository.findById(id);
    }

    public VehicleRecall createRecall(VehicleRecall vehicleRecall) {
        validateRecall(vehicleRecall);
        return vehicleRecallRepository.save(vehicleRecall);
    }

    public Optional<VehicleRecall> updateRecall(Long id, VehicleRecall vehicleRecall) {
        validateRecall(vehicleRecall);
        return vehicleRecallRepository.findById(id)
                .map(existingRecall -> {
                    existingRecall.setMake(vehicleRecall.getMake());
                    existingRecall.setModel(vehicleRecall.getModel());
                    existingRecall.setModelYear(vehicleRecall.getModelYear());
                    existingRecall.setRecallDescription(vehicleRecall.getRecallDescription());
                    existingRecall.setRecallDate(vehicleRecall.getRecallDate());
                    return vehicleRecallRepository.save(existingRecall);
                });
    }

    public boolean deleteRecall(Long id) {
        return vehicleRecallRepository.findById(id)
                .map(vehicleRecall -> {
                    vehicleRecallRepository.delete(vehicleRecall);
                    return true;
                })
                .orElse(false);
    }

    private void validateRecall(VehicleRecall vehicleRecall) {
        List<String> validationErrors = new ArrayList<>();

        // Basic validations
        if (vehicleRecall.getMake() == null || vehicleRecall.getMake().trim().isEmpty()) {
            validationErrors.add("Manufacturer is required");
        }
        if (vehicleRecall.getModel() == null || vehicleRecall.getModel().trim().isEmpty()) {
            validationErrors.add("Model is required");
        }
        if (vehicleRecall.getRecallDescription() == null || vehicleRecall.getRecallDescription().trim().isEmpty()) {
            validationErrors.add("Recall description is required");
        }
        if (vehicleRecall.getRecallDate() == null) {
            validationErrors.add("Recall date is required");
        }

        // Complex business rule validations
        if (vehicleRecall.getModelYear() < 1900 || vehicleRecall.getModelYear() > LocalDate.now().getYear() + 1) {
            validationErrors.add("Vehicle year must be between 1900 and " + (LocalDate.now().getYear() + 1));
        }
        if (vehicleRecall.getRecallDate() != null && vehicleRecall.getRecallDate().isAfter(LocalDate.now())) {
            validationErrors.add("Recall date cannot be in the future");
        }
        if (vehicleRecall.getModelYear() > LocalDate.now().getYear() && vehicleRecall.getRecallDate() != null) {
            validationErrors.add("Cannot have a recall for a future model year vehicle");
        }
        if (vehicleRecall.getMake() != null && vehicleRecall.getMake().length() > 255) {
            validationErrors.add("Manufacturer name must be less than 255 characters");
        }
        if (vehicleRecall.getModel() != null && vehicleRecall.getModel().length() > 255) {
            validationErrors.add("Model name must be less than 255 characters");
        }
        if (vehicleRecall.getRecallDescription() != null && vehicleRecall.getRecallDescription().length() > 1000) {
            validationErrors.add("Recall description must be less than 1000 characters");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", validationErrors));
        }
    }
}
