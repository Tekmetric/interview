package com.interview.mapper;

import com.interview.dto.request.MechanicShopCreationRequest;
import com.interview.dto.request.MechanicShopUpdateRequest;
import com.interview.dto.response.MechanicShopResponse;
import com.interview.model.MechanicShop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
public class MechanicShopMapper {

    private final MechanicMapper mechanicMapper;

    public MechanicShopResponse toMechanicShopResponse(MechanicShop mechanicShop) {
        if (mechanicShop == null) {
            return null;
        }
        return MechanicShopResponse.builder()
                .id(mechanicShop.getId())
                .shopName(mechanicShop.getShopName())
                .phoneNumber(mechanicShop.getPhoneNumber())
                .mechanicsResponse(mechanicMapper.toMechanicResponseSet(mechanicShop.getMechanics()))
                .email(mechanicShop.getEmail())
                .creationDate(mechanicShop.getCreationDate())
                .lastUpdateDate(mechanicShop.getLastUpdateDate())
                .build();
    }

    public MechanicShop toMechanicShop(MechanicShopCreationRequest mechanicShopCreationRequest) {
        if (mechanicShopCreationRequest == null) {
            return null;
        }
        return MechanicShop.builder()
                .shopName(mechanicShopCreationRequest.shopName())
                .phoneNumber(mechanicShopCreationRequest.phoneNumber())
                .email(mechanicShopCreationRequest.email())
                .build();
    }

    public MechanicShop updateMechanicShop(MechanicShop retrievedMechanicShop, MechanicShopUpdateRequest mechanicShopUpdateRequest) {
        Optional.ofNullable(mechanicShopUpdateRequest.shopName()).ifPresent(retrievedMechanicShop::setShopName);
        Optional.ofNullable(mechanicShopUpdateRequest.email()).ifPresent(retrievedMechanicShop::setEmail);
        Optional.ofNullable(mechanicShopUpdateRequest.phoneNumber()).ifPresent(retrievedMechanicShop::setPhoneNumber);
        return retrievedMechanicShop;
    }
}
