package com.interview.resource.service;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;

import java.util.List;

public interface IAutoShopService {

     List<AutoShopResponse> getAll();
     AutoShopResponse getById(Long id);
     AutoShopResponse create(AutoShopRequest autoShop);
     AutoShopResponse update(Long id, AutoShopRequest autoShop);
     void delete(Long id);
}
