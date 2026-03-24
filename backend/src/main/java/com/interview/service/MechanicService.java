package com.interview.service;

import com.interview.dto.request.MechanicUpdateRequest;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.MechanicMapper;
import com.interview.model.Mechanic;
import com.interview.repository.MechanicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MechanicService {

    private final MechanicRepository mechanicRepository;
    private final MechanicMapper mechanicMapper;

    @Transactional(readOnly = true)
    public Optional<Mechanic> getMechanicById(Long mechanicId) {
        return mechanicRepository.findById(mechanicId);
    }

    @Transactional(readOnly = true)
    public List<Mechanic> getAllMechanicsByShopId(Long shopId) {
        return mechanicRepository.findAllByMechanicShopId(shopId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createMechanic(Mechanic mechanic) {
        if (mechanicRepository.existsByEmail(mechanic.getEmail())) {
            throw new DuplicateResourceException(DuplicateResourceException.ErrorCode.EMAIL_ALREADY_EXISTS_IN_DB,
                    "Email already exists in DB");
        }
        if (mechanicRepository.existsByPhoneNumber(mechanic.getPhoneNumber())) {
            throw new DuplicateResourceException(DuplicateResourceException.ErrorCode.PHONE_NUMBER_ALREADY_EXISTS_IN_DB,
                    "Phone number already exists in DB");
        }
        mechanicRepository.save(mechanic);
        return mechanic.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateMechanic(Long mechanicId, MechanicUpdateRequest mechanicUpdateRequest) {
        Mechanic mechanic = mechanicRepository.findById(mechanicId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.ErrorCode.MECHANIC_NOT_FOUND,
                        "Not found mechanic with id: " + mechanicId));
        mechanicMapper.updateMechanic(mechanic, mechanicUpdateRequest);
        mechanicRepository.save(mechanic);
        return mechanic.getId();
    }

    public void deleteMechanicById(Long mechanicId) {
        log.info("Attempting to delete mechanic with id {}", mechanicId);
        mechanicRepository.deleteById(mechanicId);
        log.info("Successfully deleted mechanic with id {}", mechanicId);
    }

}
