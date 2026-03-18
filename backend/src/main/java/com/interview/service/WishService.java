package com.interview.service;

import com.interview.constant.MetricsConstants;
import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.exception.WishNotFoundException;
import com.interview.model.Wish;
import com.interview.repository.WishRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class WishService {

    private final WishRepository wishRepository;
    private final Counter wishesCameTrueCounter;

    public WishService(WishRepository wishRepository, MeterRegistry meterRegistry) {
        this.wishRepository = wishRepository;
        this.wishesCameTrueCounter = meterRegistry.counter(MetricsConstants.WISHES_CAME_TRUE_COUNT);
    }

    public Page<WishLightDTO> getAllWishes(Pageable pageable) {
        log.info("Fetching all wishes with pageable: {}", pageable);
        return wishRepository.findAllByDeletedFalse(pageable)
                .map(wish -> new WishLightDTO(wish.getName(), wish.isCameTrue(), wish.getCreatedAt(), wish.getUpdatedAt()));
    }

    public WishDTO getWishById(Long id) {
        log.info("Fetching wish with id: {}", id);
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> {
                    log.error("Wish not found with id: {}", id);
                    return new WishNotFoundException("Wish not found with id: " + id);
                });
        return mapToDTO(wish);
    }

    @Transactional
    public WishDTO createWish(WishDTO wishDTO) {
        log.info("Creating a new wish with name: {}", wishDTO.getName());
        Wish wish = new Wish();
        wish.setName(wishDTO.getName());
        wish.setComment(wishDTO.getComment());
        wish.setLink(wishDTO.getLink());
        wish.setCameTrue(false);
        wish.setDeleted(false);
        Wish savedWish = wishRepository.save(wish);
        log.info("Created wish with id: {}", savedWish.getId());
        return mapToDTO(savedWish);
    }

    @Transactional
    public WishDTO updateWish(Long id, WishDTO wishDTO) {
        log.info("Updating wish with id: {}", id);
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> {
                    log.error("Wish not found with id: {} during update", id);
                    return new WishNotFoundException("Wish not found with id: " + id);
                });
        
        wish.setName(wishDTO.getName());
        wish.setComment(wishDTO.getComment());
        wish.setLink(wishDTO.getLink());
        wish.setVersion(wishDTO.getVersion());
        
        Wish updatedWish = wishRepository.save(wish);
        log.info("Updated wish with id: {}", updatedWish.getId());
        return mapToDTO(updatedWish);
    }

    @Transactional
    public void deleteWish(Long id) {
        log.info("Deleting wish with id: {}", id);
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> {
                    log.error("Wish not found with id: {} during deletion", id);
                    return new WishNotFoundException("Wish not found with id: " + id);
                });
        wish.setDeleted(true);
        wishRepository.save(wish);
        log.info("Deleted wish with id: {}", id);
    }

    @Transactional
    public WishDTO markAsCameTrue(Long id) {
        log.info("Marking wish with id: {} as came true", id);
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> {
                    log.error("Wish not found with id: {} while marking as came true", id);
                    return new WishNotFoundException("Wish not found with id: " + id);
                });
        if (!wish.isCameTrue()) {
            wish.setCameTrue(true);
            wishesCameTrueCounter.increment();
            log.info("Wish with id: {} marked as came true", id);
        } else {
            log.info("Wish with id: {} was already came true", id);
        }
        return mapToDTO(wishRepository.save(wish));
    }

    private WishDTO mapToDTO(Wish wish) {
        WishDTO dto = new WishDTO();
        dto.setId(wish.getId());
        dto.setName(wish.getName());
        dto.setComment(wish.getComment());
        dto.setLink(wish.getLink());
        dto.setCameTrue(wish.isCameTrue());
        dto.setVersion(wish.getVersion());
        dto.setCreatedAt(wish.getCreatedAt());
        dto.setUpdatedAt(wish.getUpdatedAt());
        return dto;
    }
}
