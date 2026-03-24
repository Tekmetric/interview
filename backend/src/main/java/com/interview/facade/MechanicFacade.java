package com.interview.facade;

import com.interview.dto.request.MechanicCreationRequest;
import com.interview.dto.request.MechanicUpdateRequest;
import com.interview.dto.response.MechanicResponse;
import com.interview.exception.NotFoundException;
import com.interview.mapper.MechanicMapper;
import com.interview.mapper.MechanicsMapper;
import com.interview.model.Mechanic;
import com.interview.service.MechanicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Facade
public class MechanicFacade {

    private final MechanicService mechanicService;
    private final MechanicMapper mechanicMapper;
    private final MechanicsMapper mechanicsMapper;

    public MechanicResponse getMechanic(Long mechanicId) {
        Mechanic mechanic = getMechanicByMechanicId(mechanicId);
        log.info("Mechanic with id {} found", mechanicId);
        return mechanicMapper.toMechanicResponse(mechanic);
    }

    public List<MechanicResponse> getAllMechanics(Long shopId) {
        List<Mechanic> mechanics = getAllMechanicsByShopId(shopId);
        log.info("Mechanics with shop id {} found", shopId);
        return mechanicsMapper.toMechanicResponseList(mechanics);
    }

    public Long createMechanic(MechanicCreationRequest mechanicCreationRequest) {
        Mechanic mechanic = mechanicMapper.toMechanicEntity(mechanicCreationRequest);
        log.info("Mechanic created with id {}", mechanic.getId());
        return mechanicService.createMechanic(mechanic);
    }

    public void deleteMechanic(Long mechanicId) {
        log.info("Requested to delete mechanic with id {}", mechanicId);
        mechanicService.deleteMechanicById(mechanicId);
    }

    public Long updateMechanic(Long mechanicId, MechanicUpdateRequest mechanicUpdateRequest) {
        return mechanicService.updateMechanic(mechanicId, mechanicUpdateRequest);
    }

    private Mechanic getMechanicByMechanicId(Long mechanicId) {
        return mechanicService.getMechanicById(mechanicId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.ErrorCode.MECHANIC_NOT_FOUND,
                        "Not found mechanic with id:" + mechanicId));
    }

    private List<Mechanic> getAllMechanicsByShopId(Long shopId) {
        List<Mechanic> mechanics = mechanicService.getAllMechanicsByShopId(shopId);
        if (mechanics.isEmpty()) {
            throw new NotFoundException(NotFoundException.ErrorCode.MECHANICS_NOT_FOUND_IN_MECHANIC_SHOP,
                    "Not found mechanics for shopId:" + shopId);
        }
        return mechanics;
    }

}
