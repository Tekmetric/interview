package com.interview.adapter.out.persistence;

import com.interview.adapter.out.persistence.entities.AutoPartEntity;
import com.interview.application.port.out.AutoPartPort;
import com.interview.common.exception.AutoPartServiceException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AutoPartPersistenceAdapter implements AutoPartPort {
    private final AutoPartRepository autoPartRepository;

    @Autowired
    public AutoPartPersistenceAdapter(AutoPartRepository autoPartRepository) {
        this.autoPartRepository = autoPartRepository;
    }

    @Override
    public AutoPartEntity createAutoPart(AutoPartEntity autoPartEntity) {
        return autoPartRepository.save(autoPartEntity);
    }

    @Override
    public AutoPartEntity retrieveSingleAutoPart(UUID id) {
        return autoPartRepository.findById(id).orElseThrow(() ->
                new AutoPartServiceException("Auto part '" + id + "' not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<AutoPartEntity> retrieveAllAutoParts() {
        List<AutoPartEntity> autoParts = autoPartRepository.findAll();

        if (autoParts.isEmpty()) {
            throw new AutoPartServiceException("No auto parts found", HttpStatus.NOT_FOUND);
        }

        return autoParts;
    }

    @Transactional
    @Override
    public AutoPartEntity updateAutoPart(AutoPartEntity autoPartEntity) {
        AutoPartEntity autoPart = autoPartRepository.findById(autoPartEntity.getId())
                .orElseThrow(() -> new AutoPartServiceException("Auto part '" + autoPartEntity.getId() + "' not found", HttpStatus.NOT_FOUND));

        return autoPartRepository.save(autoPart);
    }

    @Transactional
    @Override
    public void deleteAutoPart(UUID id) {
        AutoPartEntity autoPart = autoPartRepository.findById(id)
                .orElseThrow(() -> new AutoPartServiceException("Auto part '" + id + "' not found", HttpStatus.NOT_FOUND));

        autoPartRepository.delete(autoPart);
    }
}
