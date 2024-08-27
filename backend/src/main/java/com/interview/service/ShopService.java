package com.interview.service;

import com.interview.api.dto.ShopDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopService {

  ShopDTO findById(long id);

  List<ShopDTO> getAll(Pageable pageable);

  List<ShopDTO> getAllActive(Pageable pageable);

  long createShop(ShopDTO shop);

  ShopDTO updateShop(long id, ShopDTO shop);

  void deleteShop(long id);

}
