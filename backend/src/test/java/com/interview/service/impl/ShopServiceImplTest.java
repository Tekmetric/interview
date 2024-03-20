package com.interview.service.impl;

import com.interview.domain.Invoice;
import com.interview.domain.Shop;
import com.interview.exception.ResourceNotFoundException;
import com.interview.exception.ShopUniqueConstraintViolationException;
import com.interview.repository.ShopRepository;
import com.interview.service.ShopService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShopServiceImplTest {

    @Autowired
    private ShopService shopService;

    @MockBean
    private ShopRepository shopRepository;

    private Shop shop;

    @BeforeEach
    void setUp() {
        shop = Shop.builder().id(1L).name("Test shop").location("Location").invoices(Set.of()).suppliers(Set.of()).build();
    }

    @Test
    void givenValidShopId_whenGetShopById_thenReturnShop() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));
        var actual = shopService.getShopById(1L);
        assertThat(actual).isEqualTo(shop);
    }

    @Test()
    void givenNonExistentShopId_whenGetShopById_thenExceptionIsThrown() {
        when(shopRepository.findById(2L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> shopService.getShopById(2L));
        assertThat(exception.getMessage()).isEqualTo("Shop with id:2 not found");
    }

    @Test
    void givenValidShop_whenCreateShop_thenReturnNewlyCreatedShop() {
        when(shopRepository.save(shop)).thenReturn(shop);
        var actualShop = shopService.saveShop(shop);
        assertThat(actualShop).isEqualTo(shop);
    }

    @Test
    void givenInvalidShop_whenCreateShop_thenExceptionIsThrown() {
        when(shopRepository.save(shop)).thenThrow(DataIntegrityViolationException.class);
        Exception exception = assertThrows(ShopUniqueConstraintViolationException.class, () -> shopService.saveShop(shop));
        assertThat(exception.getMessage()).isEqualTo("Shop with name:Test shop already exists!");
    }

    @Test
    void givenValidShop_whenUpdateShop_thenReturnUpdatedShop() {
        var invoiceSet = Set.of(Invoice.builder().build());
        shop.setInvoices(invoiceSet);
        when(shopRepository.existsById(1L)).thenReturn(true);
        when(shopRepository.save(shop)).thenReturn(shop);

        var actualUpdatedShop = shopService.updateShop(shop);

        assertThat(actualUpdatedShop).isEqualTo(shop);
        assertThat(actualUpdatedShop.getInvoices()).isEqualTo(invoiceSet);
    }

    @Test
    void givenNonExistingShop_whenUpdateShop_thenExceptionIsThrown() {
        when(shopRepository.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> shopService.updateShop(shop));
        assertThat(exception.getMessage()).isEqualTo("Shop with id:1 not found");
    }

    @Test
    void givenInvalidShop_whenUpdateShop_thenExceptionIsThrown() {
        when(shopRepository.existsById(1L)).thenReturn(true);
        when(shopRepository.save(shop)).thenThrow(DataIntegrityViolationException.class);
        Exception exception = assertThrows(ShopUniqueConstraintViolationException.class, () -> shopService.saveShop(shop));
        assertThat(exception.getMessage()).isEqualTo("Shop with name:Test shop already exists!");
    }

    @Test
    void givenValidShopId_whenDeleteShop_thenRepositoryDeleteMethodIsCalled() {
        shopService.deleteShop(1L);
        verify(shopRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenGetAllShops_thenRepositoryFindAllMethodIsCalled() {
        shopService.getAllShops();
        verify(shopRepository, times(1)).findAll();
    }

    @Test
    void givenPageable_whenGetAllShops_thenRepositoryFindAllMethodIsCalled() {
        var pageable = PageRequest.of(0, 10);
        shopService.getAllShops(pageable);
        verify(shopRepository, times(1)).findAll(pageable);
    }
}
