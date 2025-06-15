package com.interview.service;

import com.interview.db.repository.RepairServiceRepository;
import com.interview.dto.RepairServiceDTO;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.RepairServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class RepairServiceService {

    public static final String REPAIR_SERVICE_RESOURCE = "Repair service";
    private final RepairServiceRepository repairServiceRepository;
    private final RepairServiceMapper repairServiceMapper;

    @Transactional(readOnly = true)
    public RepairServiceDTO getRepairServiceById(Long id) {
        var repairServiceOptional = repairServiceRepository.findById(id);

        if (repairServiceOptional.isPresent()) {
            return repairServiceMapper.toDto(repairServiceOptional.get());
        } else {
            throw ResourceNotFoundException.forId(REPAIR_SERVICE_RESOURCE, id);
        }
    }

    @Transactional(readOnly = true)
    public Page<RepairServiceDTO> getAllRepairServices(Pageable pageable) {
        var repairServicePage = repairServiceRepository.findAll(pageable);

        return repairServicePage.map(repairServiceMapper::toDto);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RepairServiceDTO createRepairService(RepairServiceDTO repairServiceDTO) {
        var repairService = repairServiceMapper.toEntity(repairServiceDTO);

        var savedRepairService = repairServiceRepository.save(repairService);

        return repairServiceMapper.toDto(savedRepairService);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RepairServiceDTO updateRepairService(Long id, RepairServiceDTO repairServiceDTO) {
        if (!repairServiceRepository.existsById(id)) {
            throw ResourceNotFoundException.forId(REPAIR_SERVICE_RESOURCE, id);
        }

        repairServiceDTO.setId(id);

        var repairService = repairServiceMapper.toEntity(repairServiceDTO);

        var updatedRepairService = repairServiceRepository.save(repairService);

        return repairServiceMapper.toDto(updatedRepairService);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteRepairService(Long id) {
        if (!repairServiceRepository.existsById(id)) {
            throw ResourceNotFoundException.forId(REPAIR_SERVICE_RESOURCE, id);
        }
        
        repairServiceRepository.deleteById(id);
    }
}
