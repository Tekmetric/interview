package com.interview.controller;

import com.interview.dto.ShopDTO;
import com.interview.mapper.ShopMapper;
import com.interview.service.ShopService;
import com.interview.util.HttpLinkHeaderUriBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private static final String BAD_REQUEST_ERROR_MESSAGE = "errorMessage";
    private static final String MISSING_ID_ERROR = "Missing ID or IDs don't match";
    private static final String INVOICES_SUPPLIERS_LIST_ERROR = "Invoices and suppliers list should be provided";

    private final ShopService shopService;
    private final ShopMapper shopMapper;
    private final HttpLinkHeaderUriBuilder headerLinkUriBuilder;

    @GetMapping()
    public List<ShopDTO> getAllShops() {
        return shopMapper.modelsToDtos(shopService.getAllShops());
    }

    @GetMapping("/page")
    public ResponseEntity<?> getShopsPaginated(@RequestParam @Valid @PositiveOrZero int number, @RequestParam @Valid @PositiveOrZero int size) {
        var shopsPage = shopService.getAllShops(PageRequest.of(number, size));
        return ResponseEntity.ok().headers(headerLinkUriBuilder.buildHttpLinkHeaderForPage(shopsPage)).body(shopMapper.modelsToDtos(shopsPage.getContent()));
    }

    @GetMapping("/{id}")
    public ShopDTO getShopById(@PathVariable Long id) {
        return shopMapper.modelToDto(shopService.getShopById(id));
    }

    @PostMapping
    public ResponseEntity<?> createShop(@Valid @RequestBody ShopDTO shopDTO) {
        var createdShop = shopService.saveShop(shopMapper.dtoToModel(shopDTO));
        return ResponseEntity.created(URI.create("http://localhost:8080/api/shops/" + createdShop.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShop(@PathVariable("id") Long id, @Valid @RequestBody ShopDTO shopDTO) {
        if (!Objects.equals(id, shopDTO.getId())) {
            log.warn("Update shop: missing ID or IDs don't match. ShopDTO: " + shopDTO);
            return ResponseEntity.badRequest().body(Map.of(BAD_REQUEST_ERROR_MESSAGE, MISSING_ID_ERROR));
        }
        if (Objects.isNull(shopDTO.getInvoices()) || Objects.isNull(shopDTO.getSuppliers())) {
            log.warn("Update shop: invoices or suppliers list missing. ShopDTO: " + shopDTO);
            return ResponseEntity.badRequest().body(Map.of(BAD_REQUEST_ERROR_MESSAGE, INVOICES_SUPPLIERS_LIST_ERROR));
        }
        var shopModel = shopMapper.dtoToModel(shopDTO);
        var updatedShop = shopService.updateShop(shopModel);
        return ResponseEntity.ok(shopMapper.modelToDto(updatedShop));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateShop(@PathVariable("id") Long id, @Valid @RequestBody ShopDTO shopDTO) {
        if (!Objects.equals(id, shopDTO.getId())) {
            log.warn("Partial update shop: missing ID or IDs don't match. ShopDTO: " + shopDTO);
            return ResponseEntity.badRequest().body(Map.of(BAD_REQUEST_ERROR_MESSAGE, MISSING_ID_ERROR));
        }
        var shopModel = shopService.getShopById(id);
        shopMapper.updateShopFromDto(shopDTO, shopModel);
        var updatedShop = shopService.saveShop(shopModel);
        return ResponseEntity.ok(shopMapper.modelToDto(updatedShop));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }
}
