package com.interview.api.dto;


import com.interview.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateShopDTO {

  private final String name;
  private final String description;
  private final String address;
  private final String phoneNo;
  private final String email;

  public Shop toShop() {
    return new Shop(null, this.name, this.description, this.address, this.phoneNo, this.email,  true);
  }
}
