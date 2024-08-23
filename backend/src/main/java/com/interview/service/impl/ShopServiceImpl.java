package com.interview.service.impl;

import com.interview.api.dto.ShopDTO;
import com.interview.model.Shop;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

  private final ShopRepository shopRepository;

  @Override
  public ShopDTO findById(long id) {
    return shopRepository.findById(id)
                         .map(ShopDTO::fromShop)
                         .orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public List<ShopDTO> getAll() {
    return shopRepository.findAll().stream().map(ShopDTO::fromShop).collect(Collectors.toList());
  }

  @Override
  public long createShop(ShopDTO shop) {
    return shopRepository.saveAndFlush(shop.toShop()).getId();
  }

  @Override
  public ShopDTO updateShop(long id, ShopDTO updatedShopDTO) {
    Shop dbShop = shopRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No shop with the requested id"));

    Shop updatedShop = updateShop(dbShop, updatedShopDTO);

    return ShopDTO.fromShop(shopRepository.saveAndFlush(updatedShop));
  }

  @Override
  public void deleteShop(long id) {
    Shop dbShop = shopRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("No shop with the requested id"));
    shopRepository.delete(dbShop);
  }

  private Shop updateShop(Shop shop, ShopDTO updatedShopDTO) {
    if (shop.getId() != updatedShopDTO.getId()) {
      throw new IllegalArgumentException("No shop with the requested id");
    }
    shop.setName(updatedShopDTO.getName());
    shop.setAddress(updatedShopDTO.getAddress());
    return shop;
  }

}
