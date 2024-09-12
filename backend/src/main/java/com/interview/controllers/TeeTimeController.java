package com.interview.controllers;

import com.interview.entities.TeeTime;
import com.interview.resources.TeeTimeResource;
import com.interview.services.TeeTimeService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/tee-times")
public class TeeTimeController {
    private final TeeTimeService teeTimeService;
    private final ModelMapper modelMapper;

    public TeeTimeController(final TeeTimeService teeTimeService, final ModelMapper modelMapper) {
        this.teeTimeService = teeTimeService;
        this.modelMapper = modelMapper;
    }
    
    @GetMapping
    public List<TeeTimeResource> getAllTeeTimes() {
        return teeTimeService.getAllTeeTimes().stream()
                .map(teeTime -> modelMapper.map(teeTime, TeeTimeResource.class))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public TeeTimeResource getTeeTimeById(@PathVariable final Long id) {
        return modelMapper.map(findTeeTimeById(id), TeeTimeResource.class);
    }
    
    @PostMapping
    public TeeTimeResource createTeeTime(@RequestBody final TeeTimeResource TeeTimeResource) {
        final TeeTime teeTime = modelMapper.map(TeeTimeResource, TeeTime.class);
        return modelMapper.map(teeTimeService.createTeeTime(teeTime), TeeTimeResource.class);
    }
    
    @PutMapping("/{id}")
    public TeeTimeResource updateTeeTime(@PathVariable final Long id,
                                         @RequestBody final TeeTimeResource TeeTimeResource) {
        final TeeTime existingTeeTime = findTeeTimeById(id);
        final TeeTime updateTeeTime = modelMapper.map(TeeTimeResource, TeeTime.class);
        modelMapper.map(updateTeeTime, existingTeeTime);
        return modelMapper.map(teeTimeService.updateTeeTime(existingTeeTime), TeeTimeResource.class);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeeTime(@PathVariable final Long id) {
        findTeeTimeById(id);
        teeTimeService.deleteTeeTime(id);
    }

    private TeeTime findTeeTimeById(final Long id) {
        final TeeTime teeTime = teeTimeService.getTeeTimeById(id);

        if (teeTime == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tee time not found"
            );
        }

        return teeTime;
    }
}