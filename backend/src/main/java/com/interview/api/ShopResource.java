package com.interview.api;

import com.interview.api.dto.ShopDTO;
import com.interview.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopResource {

  private final ShopService shopService;

  @GetMapping("/{id}")
  public ShopDTO findById(@PathVariable("id") long id) {
    return shopService.findById(id);
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<ShopDTO> getAll() {
    return shopService.getAll();
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public long createShop(@RequestBody ShopDTO shop) {
    return shopService.createShop(shop);
  }

  @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ShopDTO updateShop(@PathVariable("id") long id,
                            @RequestBody ShopDTO shop) {
    return shopService.updateShop(id, shop);
  }

  @DeleteMapping("/{id}")
  public void deleteShop(@PathVariable("id") long id) {
    shopService.deleteShop(id);
  }

}
