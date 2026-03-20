package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.ServiceOrderMapper;
import com.interview.model.dto.ServiceOrderDTO;
import com.interview.model.entity.ServiceOrder;
import com.interview.model.entity.Vehicle;
import com.interview.model.enums.ServiceOrderStatus;
import com.interview.repository.ServiceOrderRepository;
import com.interview.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceOrderMapper serviceOrderMapper;

    public Page<ServiceOrderDTO> getServiceOrdersByVehicle(String vin, Pageable pageable) {
        if (!vehicleRepository.existsByVin(vin)) {
            throw new ResourceNotFoundException("Vehicle not found with VIN: " + vin);
        }

        return serviceOrderRepository.findAllByVehicles_Vin(vin, pageable)
                .map(serviceOrderMapper::toDTO);
    }

    public Page<ServiceOrderDTO> getServiceOrdersByVehicleAndStatus(String vin, ServiceOrderStatus status, Pageable pageable) {
        if (!vehicleRepository.existsByVin(vin)) {
            throw new ResourceNotFoundException("Vehicle not found with VIN: " + vin);
        }

        return serviceOrderRepository.findAllByVehicles_VinAndStatus(vin, status, pageable)
                .map(serviceOrderMapper::toDTO);
    }

    public ServiceOrderDTO addServiceOrder(String vin, ServiceOrderDTO dto) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with VIN: " + vin));

        ServiceOrder order = serviceOrderMapper.toEntity(dto);
        order.addVehicle(vehicle);

        return serviceOrderMapper.toDTO(serviceOrderRepository.save(order));
    }

    public ServiceOrderDTO updateServiceOrder(Long orderId, ServiceOrderDTO dto) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Order not found with id: " + orderId));

        serviceOrderMapper.updateEntityFromDto(dto, order);
        return serviceOrderMapper.toDTO(serviceOrderRepository.save(order));
    }

    public void removeServiceOrderFromVehicle(String vin, Long orderId) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with VIN: " + vin));

        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Order not found with id: " + orderId));

        order.removeVehicle(vehicle);

        if (order.getVehicles().isEmpty()) {
            serviceOrderRepository.delete(order);
        } else {
            serviceOrderRepository.save(order);
        }
    }
}