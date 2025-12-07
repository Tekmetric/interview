package com.interview.purchaseOrders.service.impl;

import com.interview.purchaseOrders.dto.PurchaseOrderDTO;
import com.interview.purchaseOrders.mapper.PurchaseOrderMapper;
import com.interview.purchaseOrders.model.PurchaseOrder;
import com.interview.purchaseOrders.repository.PurchaseOrderRepository;
import com.interview.purchaseOrders.service.api.PurchaseOrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    PurchaseOrderRepository purchaseOrderRepository;
    PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                    PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @Override
    @Transactional
    public PurchaseOrderDTO findById(Long id) throws NoSuchElementException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow();
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    @Transactional
    public List<PurchaseOrderDTO> findAll() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        return purchaseOrders.stream().map(purchaseOrderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public PurchaseOrderDTO create(PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO update(Long id, PurchaseOrderDTO purchaseOrderDTO) throws NoSuchElementException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow();
        purchaseOrderMapper.updateEntityFromDTO(purchaseOrderDTO, purchaseOrder);
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        purchaseOrderRepository.deleteById(id);
    }


}
