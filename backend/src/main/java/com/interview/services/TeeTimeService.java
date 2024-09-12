package com.interview.services;

import com.interview.entities.TeeTime;
import com.interview.repositories.TeeTimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeeTimeService {
    private final TeeTimeRepository teeTimeRepository;

    public TeeTimeService(final TeeTimeRepository teeTimeRepository) {
        this.teeTimeRepository = teeTimeRepository;
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
