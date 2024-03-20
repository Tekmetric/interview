package com.interview.service;

import com.interview.domain.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopService {

    List<Shop> getAllShops();

    Page<Shop> getAllShops(Pageable pageable);

    Shop getShopById(Long id);

    Shop saveShop(Shop shop);

    Shop updateShop(Shop shop);

    void deleteShop(Long id);
}
