package com.interview.service;

import com.interview.dto.WishDTO;
import com.interview.dto.WishLightDTO;
import com.interview.model.Wish;
import com.interview.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishService {

    private final WishRepository wishRepository;

    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public List<WishLightDTO> getAllWishes() {
        return wishRepository.findAllByDeletedFalse().stream()
                .map(wish -> new WishLightDTO(wish.getName(), wish.isCameTrue()))
                .collect(Collectors.toList());
    }

    public WishDTO getWishById(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new RuntimeException("Wish not found with id: " + id));
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
                .orElseThrow(() -> new RuntimeException("Wish not found with id: " + id));
        
        wish.setName(wishDTO.getName());
        wish.setComment(wishDTO.getComment());
        wish.setLink(wishDTO.getLink());
        
        return mapToDTO(wishRepository.save(wish));
    }

    @Transactional
    public void deleteWish(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new RuntimeException("Wish not found with id: " + id));
        wish.setDeleted(true);
        wishRepository.save(wish);
    }

    @Transactional
    public WishDTO markAsCameTrue(Long id) {
        Wish wish = wishRepository.findById(id)
                .filter(w -> !w.isDeleted())
                .orElseThrow(() -> new RuntimeException("Wish not found with id: " + id));
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
        return dto;
    }
}
