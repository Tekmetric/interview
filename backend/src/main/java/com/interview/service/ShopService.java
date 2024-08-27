package com.interview.service;

import com.interview.api.dto.ShopDTO;
import java.util.List;

public interface ShopService {

  ShopDTO findById(long id);

  List<ShopDTO> getAll();

  List<ShopDTO> getAllActive();

  long createShop(ShopDTO shop);

  ShopDTO updateShop(long id, ShopDTO shop);

  void deleteShop(long id);

}
