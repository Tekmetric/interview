package com.interview.service;

import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.exception.WishNotFoundException;
import com.interview.model.Wish;
import com.interview.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public Page<WishLightDTO> getAllWishes(Pageable pageable) {
        return wishRepository.findAllByDeletedFalse(pageable)
                .map(wish -> new WishLightDTO(wish.getName(), wish.isCameTrue(), wish.getCreatedAt(), wish.getUpdatedAt()));
    }

    public WishDTO getWishById(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new WishNotFoundException("Wish not found with id: " + id));
        return mapToDTO(wish);
    }

    @Transactional
    public WishDTO createWish(WishDTO wishDTO) {
        Wish wish = new Wish();
        wish.setName(wishDTO.getName());
        wish.setComment(wishDTO.getComment());
        wish.setLink(wishDTO.getLink());
        wish.setCameTrue(false);
        wish.setDeleted(false);
        return mapToDTO(wishRepository.save(wish));
    }

    @Transactional
    public WishDTO updateWish(Long id, WishDTO wishDTO) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new WishNotFoundException("Wish not found with id: " + id));
        
        wish.setName(wishDTO.getName());
        wish.setComment(wishDTO.getComment());
        wish.setLink(wishDTO.getLink());
        wish.setVersion(wishDTO.getVersion());
        
        return mapToDTO(wishRepository.save(wish));
    }

    @Transactional
    public void deleteWish(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new WishNotFoundException("Wish not found with id: " + id));
        wish.setDeleted(true);
        wishRepository.save(wish);
    }

    @Transactional
    public WishDTO markAsCameTrue(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new WishNotFoundException("Wish not found with id: " + id));
        wish.setCameTrue(true);
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
