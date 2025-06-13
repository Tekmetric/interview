package com.interview.service;


import com.interview.dto.VehicleRequestDTO;
import com.interview.dto.VehicleResponseDTO;
import com.interview.dto.VehicleSummaryDTO;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.model.CustomerEntity;
import com.interview.model.VehicleEntity;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing vehicles. Provides methods to create, read, update, and delete
 * vehicles, as well as methods to fetch vehicle summaries and details.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehicleService {

  private final VehicleRepository vehicleRepository;
  private final CustomerRepository customerRepository;
  private final VehicleMapper vehicleMapper;
  private static final String VEHICLE_NOT_FOUND = "Vehicle not found with id: %d";

  @Transactional(readOnly = true)
  public List<VehicleSummaryDTO> getAllVehicles() {
    log.debug("Fetching all vehicles");
    List<VehicleEntity> vehicles = vehicleRepository.findAllByOrderById();
    return vehicleMapper.toSummaryDTOList(vehicles);
  }

  @Transactional(readOnly = true)
  public Page<VehicleSummaryDTO> getAllVehicles(Pageable pageable) {
    log.debug("Fetching vehicles with pagination: {}", pageable);
    Page<VehicleEntity> vehicles = vehicleRepository.findAll(pageable);
    return vehicles.map(vehicleMapper::toSummaryDTO);
  }

  @Transactional(readOnly = true)
  public VehicleResponseDTO getVehicleById(Long id) {
    log.debug("Fetching vehicle with id: {}", id);
    VehicleEntity vehicle = vehicleRepository.findByIdWithCustomer(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(VEHICLE_NOT_FOUND, id)));
    return vehicleMapper.toResponseDTO(vehicle);
  }

  @Transactional(readOnly = true)
  public VehicleResponseDTO getVehicleByIdWithRepairOrders(Long id) {
    log.debug("Fetching vehicle with repair orders for id: {}", id);
    VehicleEntity vehicle = vehicleRepository.findByIdWithCustomerAndRepairOrders(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(VEHICLE_NOT_FOUND, id)));
    return vehicleMapper.toResponseDTOWithRepairOrders(vehicle);
  }

  @Transactional(readOnly = true)
  public VehicleResponseDTO getVehicleByLicensePlate(String licensePlate) {
    log.debug("Fetching vehicle with license plate: {}", licensePlate);
    VehicleEntity vehicle = vehicleRepository.findByLicensePlate(licensePlate)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Vehicle not found with license plate: " + licensePlate));
    return vehicleMapper.toResponseDTO(vehicle);
  }

  @Transactional(readOnly = true)
  public List<VehicleSummaryDTO> getVehiclesByCustomerId(Long customerId) {
    log.debug("Fetching vehicles for customer id: {}", customerId);
    List<VehicleEntity> vehicles = vehicleRepository.findByCustomerIdOrderById(customerId);
    return vehicleMapper.toSummaryDTOList(vehicles);
  }

  public VehicleResponseDTO createVehicle(VehicleRequestDTO vehicleRequest) {
    log.debug("Creating new vehicle with license plate: {}", vehicleRequest.licensePlate());

    if (vehicleRepository.existsByLicensePlate(vehicleRequest.licensePlate())) {
      throw new ResourceAlreadyExistsException(
          "Vehicle already exists with license plate: " + vehicleRequest.licensePlate());
    }

    CustomerEntity customer = customerRepository.findById(vehicleRequest.customerId())
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Customer not found with id: " + vehicleRequest.customerId()));

    VehicleEntity vehicle = vehicleMapper.toEntity(vehicleRequest, customer);

    VehicleEntity savedVehicle = vehicleRepository.save(vehicle);
    log.info("Vehicle created successfully with id: {}", savedVehicle.getId());
    return vehicleMapper.toResponseDTO(savedVehicle);
  }

  public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO vehicleRequest) {
    log.debug("Updating vehicle with id: {}", id);

    VehicleEntity existingVehicle = vehicleRepository.findByIdWithCustomer(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(VEHICLE_NOT_FOUND, id)));

    // Check if license plate is being changed and if new license plate already exists
    if (!existingVehicle.getLicensePlate().equals(vehicleRequest.licensePlate()) &&
        vehicleRepository.existsByLicensePlate(vehicleRequest.licensePlate())) {
      throw new ResourceAlreadyExistsException(
          "Vehicle already exists with license plate: " + vehicleRequest.licensePlate());
    }

    // Update fields using mapper
    CustomerEntity customer = existingVehicle.getCustomer();
    if (!existingVehicle.getCustomer().getId().equals(vehicleRequest.customerId())) {
      customer = customerRepository.findById(vehicleRequest.customerId())
          .orElseThrow(() -> new ResourceNotFoundException(
              "Customer not found with id: " + vehicleRequest.customerId()));
    }
    vehicleMapper.updateEntityFromDTO(existingVehicle, vehicleRequest, customer);

    VehicleEntity updatedVehicle = vehicleRepository.save(existingVehicle);
    log.info("Vehicle updated successfully with id: {}", updatedVehicle.getId());
    return vehicleMapper.toResponseDTO(updatedVehicle);
  }

  public void deleteVehicle(Long id) {
    log.debug("Deleting vehicle with id: {}", id);

    VehicleEntity vehicle = vehicleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(VEHICLE_NOT_FOUND, id)));
    vehicleRepository.delete(vehicle);
    log.info("Vehicle deleted successfully with id: {}", id);
  }

  @Transactional(readOnly = true)
  public long getTotalVehicles() {
    return vehicleRepository.count();
  }

  @Transactional(readOnly = true)
  public long getVehicleCountByCustomerId(Long customerId) {
    return vehicleRepository.countByCustomerId(customerId);
  }

}
