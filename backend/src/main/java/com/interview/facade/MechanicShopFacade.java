package com.interview.facade;

import com.interview.dto.request.MechanicShopCreationRequest;
import com.interview.dto.request.MechanicShopUpdateRequest;
import com.interview.dto.response.MechanicShopResponse;
import com.interview.exception.NotFoundException;
import com.interview.mapper.MechanicShopMapper;
import com.interview.model.MechanicShop;
import com.interview.service.MechanicShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Facade
public class MechanicShopFacade {

    private final MechanicShopService mechanicShopService;
    private final MechanicShopMapper mechanicShopMapper;

    public MechanicShopResponse getMechanicShopWithMechanics(Long mechanicShopId) {
        MechanicShop mechanicShop = getMechanicShopById(mechanicShopId);
        log.info("Found Mechanic Shop with Id: {}", mechanicShop.getId());
        return mechanicShopMapper.toMechanicShopResponse(mechanicShop);
    }

    public Long createEmptyMechanicShop(MechanicShopCreationRequest mechanicShopCreationRequest) {
        MechanicShop mechanicShop = mechanicShopMapper.toMechanicShop(mechanicShopCreationRequest);
        mechanicShopService.createEmptyMechanicShop(mechanicShop);
        log.info("Created Mechanic Shop with Id: {}", mechanicShop.getId());
        return mechanicShop.getId();
    }

    public Long updateMechanicShop(Long mechanicShopId, MechanicShopUpdateRequest mechanicShopUpdateRequest) {
        return mechanicShopService.updateMechanicShop(mechanicShopId, mechanicShopUpdateRequest);
    }

    private MechanicShop getMechanicShopById(Long mechanicShopId) {
        return mechanicShopService.getMechanicShopById(mechanicShopId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.ErrorCode.MECHANIC_SHOP_NOT_FOUND,
                        "Not found mechanic shop with id:" + mechanicShopId));
    }

}
