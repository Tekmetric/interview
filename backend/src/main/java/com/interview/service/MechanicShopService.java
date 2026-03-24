package com.interview.service;

import com.interview.dto.request.MechanicShopUpdateRequest;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.NotFoundException;
import com.interview.mapper.MechanicShopMapper;
import com.interview.model.MechanicShop;
import com.interview.repository.MechanicShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MechanicShopService {

    private final MechanicShopRepository mechanicShopRepository;
    private final MechanicShopMapper mechanicShopMapper;

    @Transactional(readOnly = true)
    public Optional<MechanicShop> getMechanicShopById(Long mechanicShopId) {
        return mechanicShopRepository.findWithMechanicsById(mechanicShopId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createEmptyMechanicShop(MechanicShop mechanicShop) {
        if (mechanicShopRepository.existsByEmail(mechanicShop.getEmail())) {
            throw new DuplicateResourceException(DuplicateResourceException.ErrorCode.EMAIL_ALREADY_EXISTS_IN_DB,
                    "Email already exists in DB");
        }
        if (mechanicShopRepository.existsByPhoneNumber(mechanicShop.getPhoneNumber())) {
            throw new DuplicateResourceException(DuplicateResourceException.ErrorCode.PHONE_NUMBER_ALREADY_EXISTS_IN_DB,
                    "Phone number already exists in DB");
        }
        mechanicShopRepository.save(mechanicShop);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long updateMechanicShop(Long mechanicShopId, MechanicShopUpdateRequest mechanicShopUpdateRequest) {
        MechanicShop mechanicShop = mechanicShopRepository.findById(mechanicShopId)
                .map(retrievedMechanicShop -> mechanicShopMapper.updateMechanicShop(retrievedMechanicShop, mechanicShopUpdateRequest))
                .orElseThrow(() -> new NotFoundException(NotFoundException.ErrorCode.MECHANIC_SHOP_NOT_FOUND,
                        "Not found mechanic shop with id: " + mechanicShopId));
        mechanicShopRepository.save(mechanicShop);
        return mechanicShop.getId();
    }
}
