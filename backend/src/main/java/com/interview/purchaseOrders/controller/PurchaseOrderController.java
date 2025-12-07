package com.interview.purchaseOrders.controller;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;
import com.interview.purchaseOrders.service.api.PurchaseOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/purchaseOrder")
public class PurchaseOrderController {

    PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping("/{id}")
    PurchaseOrderDTO getPurchaseOrder(@PathVariable Long id) {
        try {
            return purchaseOrderService.findById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order Not Found");
        }
    }

    @GetMapping()
    List<PurchaseOrderDTO> getPurchaseOrders() {
        return purchaseOrderService.findAll();
    }

    @PostMapping("/create")
    PurchaseOrderDTO create(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        return purchaseOrderService.create(purchaseOrderDTO);
    }

    @PutMapping("/update/{id}")
    PurchaseOrderDTO update(@PathVariable Long id, @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        try {
            return purchaseOrderService.update(id, purchaseOrderDTO);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order Not Found");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
    }


}
