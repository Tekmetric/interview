package com.interview.service.impl;

import com.interview.api.dto.ShopDTO;
import com.interview.exception.ServiceException;
import com.interview.model.Shop;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.interview.exception.ExceptionReason.BAD_REQUEST;
import static com.interview.exception.ExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

  private final ShopRepository shopRepository;

  @Override
  public ShopDTO findById(long id) {
    return shopRepository.findByIdAndActiveTrue(id)
                         .map(ShopDTO::fromShop)
                         .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));
  }

  @Override
  public List<ShopDTO> getAll(Pageable pageable) {
    return shopRepository.findAll(pageable).stream().map(ShopDTO::fromShop).collect(Collectors.toList());
  }

  @Override
  public List<ShopDTO> getAllActive(Pageable pageable) {
    return shopRepository.findByActiveTrue(pageable).stream().map(ShopDTO::fromShop).collect(Collectors.toList());
  }

  @Override
  public long createShop(ShopDTO shop) {
    return shopRepository.saveAndFlush(shop.toShop()).getId();
  }

  @Override
  public ShopDTO updateShop(long id, ShopDTO updatedShopDTO) {
    Shop dbShop = shopRepository.findById(id)
            .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));

    Shop updatedShop = updateShop(dbShop, updatedShopDTO);

    return ShopDTO.fromShop(shopRepository.saveAndFlush(updatedShop));
  }

  @Override
  public void deleteShop(long id) {
    Shop dbShop = shopRepository.findById(id)
                                .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));
    dbShop.setActive(false);
    shopRepository.saveAndFlush(dbShop);
  }

  private Shop updateShop(Shop shop, ShopDTO updatedShopDTO) {
    if (shop.getId() != updatedShopDTO.getId()) {
      throw new ServiceException("Invalid id", BAD_REQUEST);
    }
    shop.setName(updatedShopDTO.getName());
    shop.setAddress(updatedShopDTO.getAddress());
    return shop;
  }

}
