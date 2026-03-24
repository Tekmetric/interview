package com.interview.mapper;

import com.interview.dto.request.MechanicCreationRequest;
import com.interview.dto.request.MechanicUpdateRequest;
import com.interview.dto.response.MechanicResponse;
import com.interview.model.Mechanic;
import com.interview.model.MechanicShop;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface MechanicMapper {

    MechanicResponse toMechanicResponse(Mechanic mechanic);

    Set<MechanicResponse> toMechanicResponseSet(Set<Mechanic> mechanics);

    @Mapping(source = "mechanicShopId", target = "mechanicShop")
    Mechanic toMechanicEntity(MechanicCreationRequest mechanicCreationRequest);

    default MechanicShop map(Long mechanicShopId) {
        if (mechanicShopId == null) {
            return null;
        }
        return MechanicShop.builder()
                .id(mechanicShopId)
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMechanic(@MappingTarget Mechanic retrievedMechanic, MechanicUpdateRequest mechanicUpdateRequest);
}
