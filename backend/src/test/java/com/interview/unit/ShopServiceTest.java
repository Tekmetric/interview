package com.interview.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.interview.dto.shop.CreateShopDto;
import com.interview.dto.shop.ShopDto;
import com.interview.dto.shop.ShopMapper;
import com.interview.dto.shop.UpdateShopDto;
import com.interview.exception.BadRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.shop.Shop;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ShopMapper shopMapper;

    @InjectMocks
    private ShopService shopService;

    @Test
    public void createShop_whenShopDoesNotExist_shouldCreateNewShop() {
        CreateShopDto createShopDto = new CreateShopDto();
        createShopDto.setTitle("Candy Shop");

        Shop shop = new Shop();
        shop.setTitle(createShopDto.getTitle());

        when(shopRepository.findByTitleAndDeletedDateIsNull(any())).thenReturn(Optional.empty());
        when(shopRepository.save(any())).thenReturn(shop);
        when(shopMapper.createShopDtoToShop(any())).thenReturn(shop);
        when(shopMapper.shopToShopDto(any())).thenReturn(new ShopDto());

        ShopDto shopDto = shopService.createShop(createShopDto);

        assertThat(shopDto).isNotNull();
    }

    @Test(expected = BadRequestException.class)
    public void createShop_whenShopExists_shouldThrowBadRequestException() {
        CreateShopDto createShopDto = new CreateShopDto();
        createShopDto.setTitle("Candy Shop");

        when(shopRepository.findByTitleAndDeletedDateIsNull(any())).thenReturn(Optional.of(new Shop()));

        shopService.createShop(createShopDto);
    }

    @Test
    public void listShops_whenThereAreShops_shouldReturnPageOfShops() {
        Pageable pageable = Pageable.unpaged();
        List<Shop> shops = new ArrayList<>();
        shops.add(new Shop());
        shops.add(new Shop());

        Page<Shop> shopPage = new PageImpl<>(shops);

        when(shopRepository.findByTitleContainingIgnoreCaseAndDeletedDateIsNull(eq(pageable),anyString())).thenReturn(shopPage);
        when(shopMapper.shopToShopDto(any())).thenReturn(new ShopDto());

        Page<ShopDto> shopDtoPage = shopService.listShops(pageable, "Test");

        assertThat(shopDtoPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void listShops_whenThereAreNoShops_shouldReturnEmptyPage() {
        Pageable pageable = Pageable.unpaged();

        when(shopRepository.findByTitleContainingIgnoreCaseAndDeletedDateIsNull(eq(pageable),anyString())).thenReturn(Page.empty());

        Page<ShopDto> shopDtoPage = shopService.listShops(pageable, "Test");

        assertThat(shopDtoPage.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void updateShop_whenShopExists_shouldUpdateShop() {
        UUID shopId = UUID.randomUUID();
        Shop shop = new Shop();
        shop.setTitle("Candy Shop");

        ShopDto updatedShopDto = new ShopDto();
        updatedShopDto.setTitle("Updated Candy Shop");

        UpdateShopDto updateShopDto = new UpdateShopDto();
        updateShopDto.setTitle("Updated Candy Shop");

        when(shopRepository.findByIdAndDeletedDateIsNull(eq(shopId))).thenReturn(Optional.of(shop));
        when(shopMapper.updateShopDtoToShop(any())).thenReturn(new Shop());
        when(shopMapper.shopToShopDto(any())).thenReturn(updatedShopDto);

        ShopDto shopDto = shopService.updateShop(shopId, updateShopDto);

        assertThat(shopDto).isNotNull();
        assertThat(shopDto.getTitle()).isEqualTo("Updated Candy Shop");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateShop_whenShopDoesNotExist_shouldThrowResourceNotFoundException() {
        UUID shopId = UUID.randomUUID();
        UpdateShopDto updateShopDto = new UpdateShopDto();

        when(shopRepository.findByIdAndDeletedDateIsNull(eq(shopId))).thenReturn(Optional.empty());

        shopService.updateShop(shopId, updateShopDto);
    }

    @Test(expected = BadRequestException.class)
    public void updateShop_whenAnotherShopHasTheSameTitle_shouldThrowBadRequestException() {
        UUID shopId = UUID.randomUUID();
        UpdateShopDto updateShopDto = new UpdateShopDto();
        updateShopDto.setTitle("Existing Title");

        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setTitle("Old Title");

        Shop existingShop = new Shop();
        existingShop.setId(UUID.randomUUID());
        existingShop.setTitle("Existing Title");

        when(shopRepository.findByIdAndDeletedDateIsNull(eq(shopId))).thenReturn(Optional.of(shop));
        when(shopRepository.findByTitleAndDeletedDateIsNull(eq("Existing Title"))).thenReturn(Optional.of(existingShop));

        shopService.updateShop(shopId, updateShopDto);
    }
}
