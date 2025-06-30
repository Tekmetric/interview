package com.interview.service;

import com.interview.domain.dto.ShopDto;
import com.interview.domain.mapper.ShopMapper;
import com.interview.domain.entity.Shop;
import com.interview.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    private final ShopMapper shopMapper;

    @Transactional(readOnly = true)
    public List<ShopDto> findAll() {
        log.info("Fetching all shops");
        List<Shop> shops = shopRepository.findAll();
        return shopMapper.toDtoList(shops);
    }

    @Transactional(readOnly = true)
    public Page<ShopDto> findAll(Pageable pageable) {
        log.info("Fetching shops page {} with size {}", pageable.getPageNumber(), pageable.getPageSize());
        return shopRepository.findAll(pageable)
                .map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ShopDto> findById(Long id) {
        log.info("Fetching shop with id {}", id);
        var shop = shopRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Shop not found with id: " + id));
        ShopDto dto = shopMapper.toDto(shop);
        log.info("Fetched shop: {}", dto);
        return Optional.of(dto);
    }

    @Transactional
    public ShopDto save(ShopDto shop) {
        log.info("Saving new shop: {}", shop);
        Shop entity = shopMapper.toEntity(shop);
        Shop savedEntity = shopRepository.save(entity);
        ShopDto savedDto = shopMapper.toDto(savedEntity);
        return savedDto;
    }

    @Transactional
    public Optional<Shop> update(Long id, ShopDto shopDetails) {
        log.info("Updating shop with id {}", id);
        return shopRepository.findById(id)
                .map(existing -> {
                    existing.setName(shopDetails.name());
                    existing.setAddress(shopDetails.address());
                    existing.setNumberOfEmployees(shopDetails.numberOfEmployees());
                    return shopRepository.save(existing);
                });
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting shop with id {}", id);
        shopRepository.deleteById(id);
    }
}
