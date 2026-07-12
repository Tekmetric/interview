package com.interview.service;

import com.interview.domain.RepairOrder;
import com.interview.dto.RepairOrderCreateDto;
import com.interview.dto.RepairOrderDto;
import com.interview.dto.RepairOrderUpdateDto;
import com.interview.exception.ConflictException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.RepairOrderMapper;
import com.interview.repository.RepairOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;

    public RepairOrderService(RepairOrderRepository repairOrderRepository) {
        this.repairOrderRepository = repairOrderRepository;
    }

    @Transactional
    public RepairOrderDto create(RepairOrderCreateDto request) {
        RepairOrder entity = RepairOrderMapper.toEntity(request);
        return RepairOrderMapper.toDto(repairOrderRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public RepairOrderDto getById(Long id) {
        RepairOrder entity = repairOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder", id));
        return RepairOrderMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<RepairOrderDto> list(Pageable pageable) {
        return repairOrderRepository.findAll(pageable).map(RepairOrderMapper::toDto);
    }

    @Transactional
    public RepairOrderDto update(Long id, RepairOrderUpdateDto request) {
        RepairOrder entity = repairOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("RepairOrder", id));

        if (entity.getVersion() == null || !entity.getVersion().equals(request.getVersion())) {
            throw new ConflictException("RepairOrder", id, "Version mismatch. Client=" + request.getVersion() + ", Server=" + entity.getVersion());
        }

        entity.update(request.getCustomerName(), request.getDescription(), request.getStatus());

        return RepairOrderMapper.toDto(repairOrderRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repairOrderRepository.existsById(id)) {
            throw new NotFoundException("RepairOrder", id);
        }
        repairOrderRepository.deleteById(id);
    }
}
