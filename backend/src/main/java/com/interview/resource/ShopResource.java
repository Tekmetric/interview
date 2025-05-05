package com.interview.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interview.data.Shop;
import com.interview.dto.ShopRequest;
import com.interview.dto.ShopResponse;
import com.interview.service.ShopService;

@RestController
public class ShopResource {

	@Autowired
	private ShopService shopService;

	@GetMapping("/api/shop/{shopId}")
	public ShopResponse getShop(@PathVariable("shopId") Long shopId) {
		return shopService.getShopById(shopId);
	}

	@DeleteMapping("/api/shop/{shopId}")
	public void deleteShop(@PathVariable("shopId") Long shopId) {
		shopService.deleteShopById(shopId);
	}

	@PostMapping("/api/shop")
	public ShopResponse createShop(@RequestBody @Valid ShopRequest shopRequest, BindingResult errors) {
		return shopService.createShop(shopRequest, errors);
	}

	@GetMapping("/api/shops")
	public List<ShopResponse> getShops(@RequestParam(required = false) String name) {
		return shopService.findAllShops(name);
	}
}