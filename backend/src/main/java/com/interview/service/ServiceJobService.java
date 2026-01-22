package com.interview.service;

import com.interview.dto.ServiceJobDTO;
import com.interview.mapper.ServiceJobMapper;
import com.interview.model.ServiceJob;
import com.interview.repository.ServiceJobRepository;
import com.interview.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceJobService {

    private final ServiceJobRepository serviceJobRepository;
    private final VehicleRepository vehicleRepository;


    public ServiceJobService(ServiceJobRepository serviceJobRepository, VehicleRepository vehicleRepository) {
        this.serviceJobRepository = serviceJobRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Create a serviceJob.
     *
     * @param serviceJobDTO the entity to save.
     * @return the persisted entity.
     */
    public ServiceJobDTO create(ServiceJobDTO serviceJobDTO) {
        ServiceJob serviceJob = ServiceJobMapper.toEntity(serviceJobDTO);
        if (serviceJobDTO.getVehicleId() != null) {
            com.interview.model.Vehicle vehicle = vehicleRepository.findById(serviceJobDTO.getVehicleId())
                .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found with id: " + serviceJobDTO.getVehicleId()));
            serviceJob.setVehicle(vehicle);
        }
        serviceJob = serviceJobRepository.save(serviceJob);
        return ServiceJobMapper.toDto(serviceJob);
    }

    /**
     * Update a serviceJob.
     *
     * @param serviceJobDTO the entity to update.
     * @return the persisted entity.
     */
    public ServiceJobDTO update(ServiceJobDTO serviceJobDTO) {
        return serviceJobRepository.findById(serviceJobDTO.getId())
            .map(existingServiceJob -> {
                existingServiceJob.setDescription(serviceJobDTO.getDescription());
                existingServiceJob.setCreationDate(serviceJobDTO.getCreationDate());
                existingServiceJob.setStatus(serviceJobDTO.getStatus());
                existingServiceJob.setCost(serviceJobDTO.getCost());
                if (serviceJobDTO.getVehicleId() != null) {
                    com.interview.model.Vehicle vehicle = vehicleRepository.findById(serviceJobDTO.getVehicleId())
                        .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found with id: " + serviceJobDTO.getVehicleId()));
                    existingServiceJob.setVehicle(vehicle);
                }
                return serviceJobRepository.save(existingServiceJob);
            })
            .map(ServiceJobMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("ServiceJob not found!"));
    }

    /**
     * Partially updates a serviceJob.
     *
     * @param serviceJobDTO the entity to update partially.
     * @return the persisted entity.
     */
    public ServiceJobDTO partialUpdate(ServiceJobDTO serviceJobDTO) {
        return serviceJobRepository.findById(serviceJobDTO.getId())
            .map(existingServiceJob -> {
                if (serviceJobDTO.getDescription() != null) {
                    existingServiceJob.setDescription(serviceJobDTO.getDescription());
                }
                if (serviceJobDTO.getCreationDate() != null) {
                    existingServiceJob.setCreationDate(serviceJobDTO.getCreationDate());
                }
                if (serviceJobDTO.getStatus() != null) {
                    existingServiceJob.setStatus(serviceJobDTO.getStatus());
                }
                if (serviceJobDTO.getCost() != null) {
                    existingServiceJob.setCost(serviceJobDTO.getCost());
                }
                if (serviceJobDTO.getVehicleId() != null) {
                    com.interview.model.Vehicle vehicle = vehicleRepository.findById(serviceJobDTO.getVehicleId())
                        .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("Vehicle not found with id: " + serviceJobDTO.getVehicleId()));
                    existingServiceJob.setVehicle(vehicle);
                }
                return serviceJobRepository.save(existingServiceJob);
            })
            .map(ServiceJobMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("ServiceJob not found!"));
    }


    /**
     * Get all the serviceJobs.
     *
     * @param pageable the pagination information.
     * @return the page of entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceJobDTO> findAll(Pageable pageable) {
        return serviceJobRepository.findAll(pageable)
            .map(ServiceJobMapper::toDto);
    }

    /**
     * Get all the serviceJobs for a specific vehicle.
     *
     * @param vehicleId the id of the vehicle.
     * @return the page of entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceJobDTO> findByVehicleId(Long vehicleId, Pageable pageable) {
        return serviceJobRepository.findByVehicleId(vehicleId, pageable)
            .map(ServiceJobMapper::toDto);
    }

    /**
     * Get all the serviceJobs for a specific customer.
     *
     * @param customerId the id of the customer.
     * @return the page of entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceJobDTO> findByCustomerId(Long customerId, Pageable pageable) {
        return serviceJobRepository.findByVehicleCustomerId(customerId, pageable)
            .map(ServiceJobMapper::toDto);
    }

    /**
     * Get one serviceJob by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public ServiceJobDTO findOne(Long id) {
        return serviceJobRepository.findById(id)
            .map(ServiceJobMapper::toDto)
            .orElseThrow(() -> new com.interview.web.rest.errors.ResourceNotFoundException("ServiceJob not found!"));
    }

    /**
     * Delete the serviceJob by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        serviceJobRepository.deleteById(id);
    }
}
