package com.interview.service;

import com.interview.dto.shop.CreateShopDto;
import com.interview.dto.shop.ShopDto;
import com.interview.dto.shop.ShopMapper;
import com.interview.dto.shop.UpdateShopDto;
import com.interview.exception.BadRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.shop.Shop;
import com.interview.repository.ShopRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShopService {

    private ShopRepository shopRepository;
    private ShopMapper shopMapper;

    public ShopService(ShopRepository shopRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
    }

    @Transactional
    public ShopDto createShop(CreateShopDto createShopDto) {
        Optional<Shop> optionalShop = shopRepository.findByTitleAndDeletedDateIsNull(createShopDto.getTitle());
        if (optionalShop.isPresent()) {
            throw new BadRequestException("shopAlreadyExists");
        }
        Shop savedShop = shopRepository.save(shopMapper.createShopDtoToShop(createShopDto));
        return shopMapper.shopToShopDto(savedShop);
    }

    @Transactional(readOnly = true)
    public ShopDto getShop(UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedDateIsNull(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.notFound"));

        return shopMapper.shopToShopDto(shop);
    }

    @Transactional(readOnly = true)
    public Page<ShopDto> listShops(Pageable pageable, String title) {
        return shopRepository.findByTitleContainingIgnoreCaseAndDeletedDateIsNull(pageable,title).map(shopMapper::shopToShopDto);
    }

    @Transactional
    public ShopDto updateShop(UUID shopId, UpdateShopDto updateShopDto) {
        Shop shop = shopRepository.findByIdAndDeletedDateIsNull(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.notFound"));

        shopRepository.findByTitleAndDeletedDateIsNull(updateShopDto.getTitle())
                .ifPresent(shopSameTitle -> {
                    if (!shop.getId().equals(shopSameTitle.getId()))
                        throw new BadRequestException("shopAlreadyExists");
                });

        Shop shopToUpdate = shopMapper.updateShopDtoToShop(updateShopDto);
        shopToUpdate.setId(shop.getId());

        return shopMapper.shopToShopDto(shopRepository.save(shopToUpdate));
    }

    @Transactional
    public void deleteShop(UUID shopId) {
        Shop shop = shopRepository.findByIdAndDeletedDateIsNull(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("shop.notFound"));
        shop.setDeletedDate(Date.from(Instant.now()));
        shopRepository.save(shop);
    }
}
