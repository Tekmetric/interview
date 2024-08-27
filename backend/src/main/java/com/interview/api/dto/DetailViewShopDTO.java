package com.interview.api.dto;


import com.interview.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailViewShopDTO {

  private final long id;
  private final String name;
  private final String description;
  private final String address;
  private final String phoneNo;
  private final String email;

  public static DetailViewShopDTO fromShop(Shop shop) {
    return new DetailViewShopDTO(shop.getId(), shop.getName(), shop.getDescription(),
            shop.getAddress(), shop.getPhoneNo(), shop.getEmail());
  }

}
