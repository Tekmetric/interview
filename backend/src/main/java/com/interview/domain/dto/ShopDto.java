package com.interview.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.CollectionUtils;

public record ShopDto(Long id,
                      @NotBlank String name,
                      String address,
                      @NotNull @Min(0) Integer numberOfEmployees) {

    public static Optional<ShopDto> findShopWithMostEmployees(List<ShopDto> shops) {
        if (CollectionUtils.isEmpty(shops)) {
            return Optional.empty();
        }

        return shops.stream()
                    .filter(Objects::nonNull)
                    .max(Comparator.comparing(ShopDto::numberOfEmployees));
    }
}
