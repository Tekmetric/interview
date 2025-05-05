package com.interview.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.interview.dto.ShopRequest;
import com.interview.exception.BadShopRequestException;
import com.interview.exception.ShopNotFoundException;
import com.interview.repository.ShopRepository;

class ShopServiceTest {

	private ShopService shopService;

	private ShopRepository shopRepository;

	@BeforeEach
	void setUp() {
		shopRepository = mock(ShopRepository.class);
		shopService = new ShopService(shopRepository);
	}

	@Test
	void getShopById_noShop_ThrowsException() {
		when(shopRepository.findById(any(Long.class)))
				.thenReturn(Optional.empty());

		ShopNotFoundException thrown = assertThrows(
				ShopNotFoundException.class,
				() -> shopService.getShopById(1L)
		);

		assertEquals("Shop not found", thrown.getMessage());
	}

	@Test
	void findAllShops_nullName_shouldCallfindAllByOrderByNameAsc() {
		shopService.findAllShops(null);
		verify(shopRepository, times(0)).findAllByNameOrderByNameAsc(null);
		verify(shopRepository, times(1)).findAllByOrderByNameAsc();
	}

	@Test
	void findAllShops_blankName_shouldCallfindAllByOrderByNameAsc() {
		shopService.findAllShops("");
		verify(shopRepository, times(0)).findAllByNameOrderByNameAsc("");
		verify(shopRepository, times(1)).findAllByOrderByNameAsc();
	}

	@Test
	void findAllShops_populated_shouldCallfindAllByOrderByNameAsc() {
		shopService.findAllShops("1");
		verify(shopRepository, times(1)).findAllByNameOrderByNameAsc("1");
		verify(shopRepository, times(0)).findAllByOrderByNameAsc();
	}

	@Test
	void createShop_withErrors_shouldThrowException() {
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("field", "error", "error message")));

		BadShopRequestException thrown = assertThrows(
				BadShopRequestException.class,
				() -> shopService.createShop(new ShopRequest(), bindingResult)
		);

		assertEquals("Invalid shop request: Field error in object 'field' on field 'error': rejected value [null]; codes []; arguments []; default message [error message]", thrown.getMessage());
	}
}