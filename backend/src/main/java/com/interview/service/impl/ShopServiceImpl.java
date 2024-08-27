package com.interview.service.impl;

import com.interview.api.dto.*;
import com.interview.exception.ServiceException;
import com.interview.model.Shop;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.interview.exception.ExceptionReason.BAD_REQUEST;
import static com.interview.exception.ExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

  private final ShopRepository shopRepository;

  @Override
  public DetailViewShopDTO findById(long id) {
    return shopRepository.findByIdAndActiveTrue(id)
                         .map(DetailViewShopDTO::fromShop)
                         .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));
  }

  @Override
  public List<ListViewShopDTO> getAll(Pageable pageable) {
    return shopRepository.findAll(pageable).stream().map(ListViewShopDTO::fromShop).toList();
  }

  @Override
  public List<ListViewShopDTO> getAllActive(Pageable pageable) {
    return shopRepository.findByActiveTrue(pageable).stream().map(ListViewShopDTO::fromShop).toList();
  }

  @Override
  public long createShop(CreateShopDTO dto) {
    return shopRepository.saveAndFlush(dto.toShop()).getId();
  }

  @Override
  public DetailViewShopDTO updateShop(long id, UpdateShopDTO dto) {
    Shop dbShop = shopRepository.findById(id)
            .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));

    Shop updatedShop = updateShop(dbShop, dto);

    return DetailViewShopDTO.fromShop(shopRepository.saveAndFlush(updatedShop));
  }

  @Override
  public void deleteShop(long id) {
    Shop dbShop = shopRepository.findById(id)
                                .orElseThrow(() -> new ServiceException("Invalid id", NOT_FOUND));
    dbShop.setActive(false);
    shopRepository.saveAndFlush(dbShop);
  }

  private Shop updateShop(Shop shop, UpdateShopDTO updatedShopDTO) {
    if (shop.getId() != updatedShopDTO.getId()) {
      throw new ServiceException("Invalid id", BAD_REQUEST);
    }
    shop.setName(updatedShopDTO.getName());
    shop.setDescription(updatedShopDTO.getDescription());
    shop.setAddress(updatedShopDTO.getAddress());
    shop.setPhoneNo(updatedShopDTO.getPhoneNo());
    shop.setEmail(updatedShopDTO.getEmail());

    return shop;
  }

}
