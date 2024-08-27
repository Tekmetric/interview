package com.interview.api.dto;


import com.interview.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateShopDTO {

  private final long id;
  private final String name;
  private final String description;
  private final String address;
  private final String phoneNo;
  private final String email;

  public Shop toShop() {
    return new Shop(this.id, this.name, this.description, this.address, this.phoneNo, this.email,  true);
  }
}
