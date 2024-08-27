package com.interview.api.dto;


import com.interview.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopDTO {

  private final long id;
  private final String name;
  private final String address;

  public static ShopDTO fromShop(Shop shop) {
    return new ShopDTO(shop.getId(), shop.getName(), shop.getAddress());
  }

  public Shop toShop() {
    return new Shop(this.id, this.name, this.address, true);
  }
}
