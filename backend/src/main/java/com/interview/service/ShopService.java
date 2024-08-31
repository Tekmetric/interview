package com.interview.service;

import com.interview.api.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopService {

  DetailViewShopDTO findById(long id);

  List<ListViewShopDTO> getAll(Pageable pageable);

  List<ListViewShopDTO> getAllActive(Pageable pageable);

  long createShop(CreateShopDTO shop);

  DetailViewShopDTO updateShop(long id, UpdateShopDTO shop);

  void deleteShop(long id);

}
