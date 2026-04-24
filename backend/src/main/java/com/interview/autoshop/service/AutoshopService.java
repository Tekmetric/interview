package com.interview.autoshop.service;

import com.interview.autoshop.controller.dto.CreateAutoshopRequest;
import com.interview.autoshop.controller.dto.UpdateAutoshopRequest;
import com.interview.autoshop.repository.AutoshopEntity;
import com.interview.autoshop.repository.AutoshopRepository;
import com.interview.autoshop.service.domain.Autoshop;
import com.interview.autoshop.service.domain.AutoshopMapper;
import com.interview.error.exception.AutoshopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AutoshopService {

    private final AutoshopRepository repository;
    private final AutoshopMapper mapper;

    @Transactional(readOnly = true)
    public Autoshop findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new AutoshopNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Autoshop> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    public Autoshop create(CreateAutoshopRequest request) {
        Autoshop incoming = mapper.fromCreate(request);
        AutoshopEntity saved = repository.save(mapper.toDao(incoming));
        return mapper.toDomain(saved);
    }

    public Autoshop replace(Long id, UpdateAutoshopRequest request) {
        AutoshopEntity managed = repository.findById(id)
                .orElseThrow(() -> new AutoshopNotFoundException(id));
        mapper.applyUpdate(request, managed);
        return mapper.toDomain(repository.save(managed));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AutoshopNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
