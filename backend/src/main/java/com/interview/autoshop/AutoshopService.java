package com.interview.autoshop;

import com.interview.autoshop.dao.AutoshopRepository;
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
}
