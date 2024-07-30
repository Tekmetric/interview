package com.interview.autoshop.services.impl;

import com.interview.autoshop.dto.CarDto;
import com.interview.autoshop.dto.ClientDto;
import com.interview.autoshop.dto.ServiceRequestDto;
import com.interview.autoshop.dto.create.CreateServiceRequestDto;
import com.interview.autoshop.enums.Status;
import com.interview.autoshop.exceptions.CarNotFoundException;
import com.interview.autoshop.exceptions.InvalidStatusException;
import com.interview.autoshop.exceptions.ServiceRequestNotFoundException;
import com.interview.autoshop.model.Car;
import com.interview.autoshop.model.ServiceRequest;
import com.interview.autoshop.repositories.ServiceRequestRepository;
import com.interview.autoshop.services.CarService;
import com.interview.autoshop.services.ServiceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    private final CarService carService;

    @Autowired
    public ServiceRequestServiceImpl(final ServiceRequestRepository serviceRequestRepository, final CarService carService){
        this.serviceRequestRepository = serviceRequestRepository;
        this.carService = carService;
    }

    private ServiceRequest createDtoToEntity(CreateServiceRequestDto serviceRequestDto){
        Optional<Car> car = carService.getEntityById(serviceRequestDto.getCarId());
        if(!car.isPresent()){
            throw new CarNotFoundException();
        }
        return ServiceRequest.builder()
                .car(car.get())
                .work(serviceRequestDto.getWork())
                .creationDate(serviceRequestDto.getCreationDate() == null ? LocalDateTime.now() : serviceRequestDto.getCreationDate())
                .status(serviceRequestDto.getStatus())
                .estimatedCharge(serviceRequestDto.getEstimatedCharge())
                .charge(serviceRequestDto.getCharge())
                .estimatedCompletionTime(serviceRequestDto.getEstimatedCompletionTime())
                .completionTime(serviceRequestDto.getCompletionTime())
                .build();
    }

    private ServiceRequest dtoToEntity(ServiceRequestDto serviceRequestDto){

        return ServiceRequest.builder()
                .id(serviceRequestDto.getId())
                .car(Car.builder()
                        .id(serviceRequestDto.getCar().getId())
                        .vin(serviceRequestDto.getCar().getVin())
                        .color(serviceRequestDto.getCar().getColor())
                        .make(serviceRequestDto.getCar().getMake())
                        .model(serviceRequestDto.getCar().getModel())
                        .build())
                .work(serviceRequestDto.getWork())
                .status(serviceRequestDto.getStatus())
                .creationDate(serviceRequestDto.getCreationDate())
                .charge(serviceRequestDto.getCharge())
                .estimatedCompletionTime(serviceRequestDto.getEstimatedCompletionTime())
                .estimatedCharge(serviceRequestDto.getEstimatedCharge())
                .completionTime(serviceRequestDto.getCompletionTime())
                .build();
    }

    private ServiceRequestDto entityToDto(ServiceRequest serviceRequest){
        return ServiceRequestDto.builder()
                .id(serviceRequest.getId())
                .car(CarDto.builder()
                        .id(serviceRequest.getCar().getId())
                        .vin(serviceRequest.getCar().getVin())
                        .color(serviceRequest.getCar().getColor())
                        .make(serviceRequest.getCar().getMake())
                        .model(serviceRequest.getCar().getModel())
                        .owner(ClientDto.builder()
                                .id(serviceRequest.getCar().getOwner().getId())
                                .name(serviceRequest.getCar().getOwner().getName())
                                .phone(serviceRequest.getCar().getOwner().getPhone())
                                .email(serviceRequest.getCar().getOwner().getEmail())
                                .build())
                        .licensePlate(serviceRequest.getCar().getLicensePlate())
                        .build())
                .work(serviceRequest.getWork())
                .status(serviceRequest.getStatus())
                .creationDate(serviceRequest.getCreationDate())
                .estimatedCompletionTime(serviceRequest.getEstimatedCompletionTime())
                .estimatedCharge(serviceRequest.getEstimatedCharge())
                .completionTime(serviceRequest.getCompletionTime())
                .charge(serviceRequest.getCharge())
                .build();
    }

    @Override
    public Optional<ServiceRequestDto> findById(Long id) {
        final Optional<ServiceRequest> serviceRequest = serviceRequestRepository.findById(id);
        return serviceRequest.map(this::entityToDto);
    }

    @Override
    public List<ServiceRequestDto> findAll(Boolean isOpen, String email) {
        String status = isOpen ? "completed" : "";
        final List<ServiceRequest> requests = serviceRequestRepository.findByCarOwnerEmailStartsIgnoreCaseWithAndStatusNotLike(email, status);
        return requests.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public ServiceRequestDto create(CreateServiceRequestDto serviceRequestDto) {
        ServiceRequest request = createDtoToEntity(serviceRequestDto);

        return save(request);
    }

    private ServiceRequestDto save(ServiceRequest request){
        validateSave(request);
        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        return entityToDto(savedRequest);
    }

    @Override
    public void deleteById(Long id) {
        if(!isRequestPresent(id)){
            log.debug("Service request with id not found : " + id);
        }
        else{
            serviceRequestRepository.deleteById(id);
        }
    }

    @Override
    public ServiceRequestDto update(Long id, CreateServiceRequestDto serviceRequestDto) {
        if(!isRequestPresent(id)){
            log.debug("Service request with id not found : " + id);
            throw new ServiceRequestNotFoundException();
        }
        ServiceRequest request = createDtoToEntity(serviceRequestDto);
        request.setId(id);
        return save(request);
    }

    @Override
    public boolean isRequestPresent(Long id) {
        return serviceRequestRepository.existsById(id);
    }

    /*
     *      Ideally, this should be defined in a separate validation service
     *      Also, there need to be other validations,
     *      for example dates, estimated completion date(&time) and completion date cannot be before request creation date
     */

    private void validateSave(ServiceRequest request){
        String status = request.getStatus();
        try{
            Status statusEnum = Status.valueOf(status.toUpperCase());
            request.setStatus(status.toLowerCase());
        } catch(IllegalArgumentException e){
            throw new InvalidStatusException();
        }
    }
}
