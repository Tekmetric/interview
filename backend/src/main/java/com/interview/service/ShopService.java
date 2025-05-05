package com.interview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.interview.data.Shop;
import com.interview.dto.ShopRequest;
import com.interview.dto.ShopResponse;
import com.interview.exception.BadShopRequestException;
import com.interview.exception.ShopNotFoundException;
import com.interview.repository.ShopRepository;

@Service
public class ShopService {

	private static final Logger log = LoggerFactory.getLogger(ShopService.class);

	@Autowired
	private ShopRepository shopRepository;

	public ShopResponse getShopById(Long shopId) {
		return shopRepository.findById(shopId).map(ShopResponse::new).orElseThrow(ShopNotFoundException::new);
	}


	public List<ShopResponse> findAllShops(String name) {
		if (name != null) {
			return shopRepository.findAllByNameOrderByNameAsc(name)
					.stream().map(ShopResponse::new)
					.collect(Collectors.toList());
		}
		return shopRepository.findAllByOrderByNameAsc()
				.stream().map(ShopResponse::new)
				.collect(Collectors.toList());
	}

	public void deleteShopById(Long shopId) {
		shopRepository.deleteById(shopId);
	}

	public ShopResponse createShop(ShopRequest shopRequest, BindingResult errors) {
		if (errors.hasErrors()) {
			String firstError = errors.getFieldErrors().get(0).toString();
			throw new BadShopRequestException("Invalid shop request: " + firstError);
		}

		Shop shop = new Shop();
		shop.setName(shopRequest.getName());
		shop.setAddress1(shopRequest.getAddress1());
		shop.setAddress2(shopRequest.getAddress2());
		shop.setCity(shopRequest.getCity());
		shop.setState(shopRequest.getState());
		shop.setZip(shopRequest.getZip());
		shop.setPhoneNumber(shopRequest.getPhoneNumber());
		shop.setEmail(shopRequest.getEmail());
		shop.setWebsite(shopRequest.getWebsite());
		shop.setCreated(LocalDateTime.now());
		shop.setModified(LocalDateTime.now());
		return new ShopResponse(shopRepository.save(shop));
	}
}
