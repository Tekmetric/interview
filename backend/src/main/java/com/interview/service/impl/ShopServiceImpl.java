package com.interview.service.impl;

import com.interview.domain.Shop;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.ShopUniqueConstraintViolationException;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {

    private static final String SHOP_NOT_FOUND_MESSAGE = "Shop with id:%s not found";
    private static final String SHOP_ALREADY_EXISTING_MESSAGE = "Shop with name:%s already exists!";

    private final ShopRepository shopRepository;

    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @Override
    public Page<Shop> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable);
    }

    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(SHOP_NOT_FOUND_MESSAGE, id)));
    }

    @Override
    @Transactional
    public Shop saveShop(Shop shop) {
        shop.getInvoices().forEach(invoice -> invoice.setShop(shop));
        return save(shop);
    }

    @Override
    @Transactional
    public Shop updateShop(Shop shop) {
        if (shopRepository.existsById(shop.getId())) {
            shop.getInvoices().forEach(invoice -> invoice.setShop(shop));
            return save(shop);
        }
        throw new ResourceNotFoundException(String.format(SHOP_NOT_FOUND_MESSAGE, shop.getId()));
    }

    @Override
    public void deleteShop(Long id) {
        shopRepository.deleteById(id);
    }

    private Shop save(Shop shop) {
        try {
            return shopRepository.save(shop);
        } catch (DataIntegrityViolationException ex) {
            log.error(ex.getMessage());
            throw new ShopUniqueConstraintViolationException(String.format(SHOP_ALREADY_EXISTING_MESSAGE, shop.getName()));
        }
    }
}
