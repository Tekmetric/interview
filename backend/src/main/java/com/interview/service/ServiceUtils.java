package com.interview.service;

import com.interview.dto.page.PageResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceUtils {

  public static <T> PageResponseDTO<T> toPageResponseDTO(final Page<T> page) {
    return PageResponseDTO.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .build();
  }
}
