package com.interview.mapper;

import com.interview.domain.Shop;
import com.interview.dto.ShopDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    ShopDTO modelToDto(Shop model);

    Shop dtoToModel(ShopDTO shopDTO);

    List<ShopDTO> modelsToDtos(List<Shop> shopList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateShopFromDto(ShopDTO shopDTO, @MappingTarget Shop model);
}
