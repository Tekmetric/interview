package com.interview.services;

import com.interview.controllers.TeeTimeController;
import com.interview.entities.TeeTime;
import com.interview.repositories.TeeTimeRepository;
import javassist.NotFoundException;
import javassist.tools.web.BadHttpRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeeTimeService {
    private final TeeTimeRepository teeTimeRepository;
    private final ModelMapper modelMapper;

    public TeeTimeService(final TeeTimeRepository teeTimeRepository, final ModelMapper modelMapper) {
        this.teeTimeRepository = teeTimeRepository;
        this.modelMapper = modelMapper;
    }

    public List<TeeTime> getAllTeeTimes() {
        return teeTimeRepository.findAll();
    }

    public TeeTime getTeeTimeById(final Long id) {
        return teeTimeRepository.findById(id).orElse(null);
    }

    public TeeTime createTeeTime(final TeeTime teeTime) {
        return teeTimeRepository.save(teeTime);
    }

    public TeeTime updateTeeTime(final TeeTime teeTimeToSave) {
        return teeTimeRepository.save(teeTimeToSave);
    }

    public void deleteTeeTime(final Long id) {
        teeTimeRepository.deleteById(id);
    }
}
