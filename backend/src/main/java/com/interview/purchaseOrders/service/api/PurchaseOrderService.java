package com.interview.purchaseOrders.service.api;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDTO findById(Long id);

    List<PurchaseOrderDTO> findAll();

    PurchaseOrderDTO create(PurchaseOrderDTO purchaseOrderDTO);

    PurchaseOrderDTO update(Long id, PurchaseOrderDTO purchaseOrderDTO);

    void delete(Long id);
}
