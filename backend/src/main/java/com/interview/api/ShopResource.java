package com.interview.api;

import com.interview.api.dto.ShopDTO;
import com.interview.service.ShopService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopResource {

  private final ShopService shopService;

  @GetMapping("/{id}")
  public ShopDTO findById(@PathVariable("id") long id) {
    return shopService.findById(id);
  }

  @GetMapping
  public List<ShopDTO> getAll() {
    return shopService.getAll();
  }

  @PostMapping
  public long createShop(@RequestBody ShopDTO shop) {
    return shopService.createShop(shop);
  }

  @PutMapping("/{id}")
  public ShopDTO updateShop(@PathVariable("id") long id,
                            @RequestBody ShopDTO shop) {
    return shopService.updateShop(id, shop);
  }

  @DeleteMapping("/{id}")
  public void deleteShop(@PathVariable("id") long id) {
    shopService.deleteShop(id);
  }

}
