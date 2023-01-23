package com.interview.dto.shop;

import com.interview.model.shop.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    ShopDto shopToShopDto(Shop shop);
    Shop shopDtoToShop(ShopDto shopDto);
    Shop createShopDtoToShop(CreateShopDto createShopDto);
    Shop updateShopDtoToShop(UpdateShopDto updateShopDto);

}

