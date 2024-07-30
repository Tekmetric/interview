package com.interview.autoshop.controllers;

import com.interview.autoshop.dto.ServiceRequestDto;
import com.interview.autoshop.dto.create.CreateServiceRequestDto;
import com.interview.autoshop.services.ServiceRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public ServiceRequestController(final ServiceRequestService serviceRequestService){
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping(path = "/api/service-requests/{id}")
    public ResponseEntity<ServiceRequestDto> retrieveRequestById(@PathVariable final Long id){
        final Optional<ServiceRequestDto> requestDto = serviceRequestService.findById(id);
        if(requestDto.isPresent()){
            return new ResponseEntity<ServiceRequestDto>(requestDto.get(), HttpStatus.OK);
        }
        return new ResponseEntity<ServiceRequestDto>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/api/service-requests")
    public ResponseEntity<List<ServiceRequestDto>> retrieveRequests(@RequestParam(defaultValue = "false") Boolean isOpen,
                                                                    //@RequestParam(required = false) Long carId,
                                                                    @RequestParam(defaultValue = "") String email){
        List<ServiceRequestDto> requests = serviceRequestService.findAll(isOpen, email);
        return new ResponseEntity<List<ServiceRequestDto>>(requests, HttpStatus.OK);
    }

    @PostMapping(path = "/api/service-requests")
    public ResponseEntity<ServiceRequestDto> createRequest(@RequestBody CreateServiceRequestDto serviceRequestDto){
        ServiceRequestDto savedRequestDto = serviceRequestService.create(serviceRequestDto);
        return new ResponseEntity<ServiceRequestDto>(savedRequestDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/api/service-requests/{id}")
    public ResponseEntity deleteRequest(@PathVariable final Long id){
        serviceRequestService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/api/service-requests/{id}")
    public ResponseEntity<ServiceRequestDto> updateRequest(@PathVariable Long id, @RequestBody CreateServiceRequestDto serviceRequestDto){
        ServiceRequestDto updatedRequest = serviceRequestService.update(id, serviceRequestDto);
        return new ResponseEntity<ServiceRequestDto>(updatedRequest, HttpStatus.OK);
    }
}
