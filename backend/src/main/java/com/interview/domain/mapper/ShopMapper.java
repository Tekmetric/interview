package com.interview.domain.mapper;

import com.interview.domain.dto.ShopDto;
import com.interview.domain.entity.Shop;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ShopMapper {
    ShopDto toDto(Shop shop);
    Shop toEntity(ShopDto shopDto);
    List<ShopDto> toDtoList(List<Shop> shops);
    List<Shop> toEntityList(List<ShopDto> shopDtos);
}
