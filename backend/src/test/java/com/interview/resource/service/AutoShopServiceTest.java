package com.interview.resource.service;

import com.interview.resource.dto.AutoShopRequest;
import com.interview.resource.dto.AutoShopResponse;
import com.interview.resource.entity.AutoShop;
import com.interview.resource.mapper.AutoShopMapper;
import com.interview.resource.mocks.AutoShopMocks;
import com.interview.resource.repository.IAutoShopRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoShopServiceTest {

    @Mock
    private IAutoShopRepository repository;

    @Mock
    private AutoShopMapper mapper;

    @InjectMocks
    private AutoShopService service;

    @InjectMocks
    private AutoShopMocks mocks;

    private List<AutoShop> autoShops;
    private List<AutoShopResponse> autoShopResponses;

    @BeforeEach
    void setup () {
        autoShops = mocks.getAutoShops();
        autoShopResponses = mocks.getAutoShopsResponses();
    };

    @Test
    void shouldReturnAllAutoShops() {
        //given
        when(repository.findAll()).thenReturn(autoShops);
        when(mapper.toListResponse(autoShops)).thenReturn(autoShopResponses);

        //when
        List<AutoShopResponse> result = service.getAll();

        //assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Discount Tires", result.get(0).getName());
        verify(repository).findAll();
        verify(mapper).toListResponse(autoShops);
    }

    @Test
    void shouldReturnAutoShopById() {
        //given
        when(repository.findById(1L)).thenReturn(Optional.of(autoShops.get(0)));
        when(mapper.toResponse(autoShops.get(0))).thenReturn(autoShopResponses.get(0));

        //when
        AutoShopResponse result = service.getById(1L);

        //assert
        Assertions.assertEquals("Discount Tires", result.getName());
        verify(repository).findById(1L);
        verify(mapper).toResponse(autoShops.get(0));
    }

    @Test
    void shouldThrowAutoShopNotFound() {
        //given
        when(repository.findById(3L)).thenReturn(Optional.empty());

        //when
       assertThrows(EntityNotFoundException.class, () -> service.getById(3L));

        //assert
        verify(repository).findById(3L);
    }

    @Test
    void shouldCreateAutoShop() {
        AutoShop newShop = mocks.createShop();

        AutoShopRequest newRequest = mocks.createShopRequest();

        when(repository.save(mapper.toEntity(newRequest))).thenReturn(newShop);
        AutoShopResponse newShopResponse = mocks.createShopResponse();
        when(mapper.toResponse(newShop)).thenReturn(newShopResponse);

        AutoShopResponse savedShop = service.create(newRequest);

        assertNotNull(savedShop);
        assertEquals("Long Star Mechanics", savedShop.getName());
        verify(repository).save(mapper.toEntity(newRequest));
        verify(mapper).toResponse(newShop);

    }

    @Test
    void shouldUpdateAutoShop() {
        AutoShop updatedAutoShop = mocks.createShop();
        AutoShopRequest newRequest = mocks.createShopRequest();
        AutoShopResponse newShopResponse = mocks.createShopResponse();

        when(repository.findById(3L)).thenReturn(Optional.of(updatedAutoShop));
        when(repository.save(any(AutoShop.class))).thenReturn(updatedAutoShop);
        when(mapper.toResponse(updatedAutoShop)).thenReturn(newShopResponse);

        AutoShopResponse response = service.update(3L, newRequest);

        assertEquals("Long Star Mechanics", response.getName());
        verify(repository).findById(3L);
        verify(repository).save(updatedAutoShop);
        verify(mapper).toResponse(updatedAutoShop);
    }

    @Test
    void shouldThrowWhenUpdatingMissingAutoShop() {
        //given
        AutoShopRequest newRequest = mocks.createShopRequest();
        when(repository.findById(5L)).thenReturn(Optional.empty());

        //when
        assertThrows(EntityNotFoundException.class, () -> service.update(5L,newRequest ));

        //assert
        verify(repository).findById(5L);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteAutoShop() {
        //given
        AutoShop shop = mocks.createShop();
        when(repository.findById(1L)).thenReturn(Optional.of(shop));

        //when
        service.delete(1L);

        //assert
        verify(repository).delete(any(AutoShop.class));
    }

    @Test
    void shouldThrowWhenDeletingMissingAutoShop() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(EntityNotFoundException.class, () -> service.delete(1L));

        //assert
        verify(repository, never()).delete(any());
    }

}
