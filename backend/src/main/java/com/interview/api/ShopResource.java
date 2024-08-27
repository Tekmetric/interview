package com.interview.api;

import com.interview.api.dto.ShopDTO;
import com.interview.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Shop", description = "Shop Management APIs")
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopResource {

  private final ShopService shopService;

  @Operation(
          summary = "Retrieve a Shop by Id",
          description = "Get a Shop object by specifying its Id")
  @ApiResponses({
          @ApiResponse(responseCode = "200",
                  content = { @Content(
                  schema = @Schema(implementation = ShopDTO.class),
                  mediaType = "application/json") }),
          @ApiResponse(responseCode = "404", description = "The Shop with given Id was not found")
  })
  @Parameters({
          @Parameter(name = "id", description = "The Id to search the Shop by")
  })
  @GetMapping("/{id}")
  public ShopDTO findById(@PathVariable("id") long id) {
    return shopService.findById(id);
  }

  @GetMapping(value = "/all", produces = APPLICATION_JSON_VALUE)
  public List<ShopDTO> getAll(@PageableDefault(size = 5, sort = "name") final Pageable pageable) {
    return shopService.getAll(pageable);
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public List<ShopDTO> getAllActive(@PageableDefault(size = 5, sort = "name") final Pageable pageable) {
    return shopService.getAllActive(pageable);
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



