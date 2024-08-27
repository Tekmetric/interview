package com.interview.api.dto;


import com.interview.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListViewShopDTO {

  private final long id;
  private final String name;
  private final String address;

  public static ListViewShopDTO fromShop(Shop shop) {
    return new ListViewShopDTO(shop.getId(), shop.getName(), shop.getAddress());
  }

}
