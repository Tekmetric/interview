package com.interview.resource.mapper;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.entity.AutoShop;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AutoShopMapper {

    public AutoShop toEntity(AutoShopRequest request) {
        AutoShop autoShop = new AutoShop();
        autoShop.setName(request.getName());
        autoShop.setAddress(request.getAddress());
        autoShop.setCity(request.getCity());
        autoShop.setState(request.getState());
        autoShop.setContractYears(request.getContractYears());
        return autoShop;
    }

    public AutoShopResponse toResponse(AutoShop autoShop) {
        AutoShopResponse autoShopResponse = new AutoShopResponse();
        autoShopResponse.setId(autoShop.getId());
        autoShopResponse.setName(autoShop.getName());
        autoShopResponse.setAddress(autoShop.getAddress());
        autoShopResponse.setCity(autoShop.getCity());
        autoShopResponse.setState(autoShop.getState());
        autoShopResponse.setContractYears(autoShop.getContractYears());
        return autoShopResponse;
    }

    public List<AutoShopResponse> toListResponse(List<AutoShop> autoShops) {
        List<AutoShopResponse> autoShopResponses = new ArrayList<>();
        for(AutoShop autoShop : autoShops) {
            autoShopResponses.add(this.toResponse(autoShop));
        }
        return autoShopResponses;
    }
}
