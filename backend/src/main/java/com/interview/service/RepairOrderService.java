package com.interview.service;

import com.interview.dto.PageResponse;
import com.interview.dto.RepairLineItemRequest;
import com.interview.dto.RepairLineItemResponse;
import com.interview.dto.RepairOrderCreateRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.dto.RepairOrderUpdateRequest;
import com.interview.entity.RepairLineItem;
import com.interview.entity.RepairOrder;
import com.interview.entity.RepairOrderStatus;
import com.interview.events.RepairOrderCompletedEvent;
import com.interview.exception.ConflictException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import com.interview.spec.RepairOrderSpecifications;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class RepairOrderService {

    private final RepairOrderRepository repo;
    private final ApplicationEventPublisher publisher;

    public RepairOrderService(RepairOrderRepository repo, ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    @Transactional
    public RepairOrderResponse create(RepairOrderCreateRequest req) {
        if (repo.existsByOrderNumber(req.orderNumber())) {
            throw new IllegalArgumentException("orderNumber already exists");
        }

        RepairOrder order = RepairOrder.builder()
                .orderNumber(req.orderNumber())
                .vin(req.vin())
                .vehicleYear(req.vehicleYear())
                .vehicleMake(req.vehicleMake())
                .vehicleModel(req.vehicleModel())
                .customerName(req.customerName())
                .customerPhone(req.customerPhone())
                .status(RepairOrderStatus.OPEN)
                .build();

        if (req.lineItems() != null) {
            req.lineItems().forEach(liReq -> order.addLineItem(buildLineItem(liReq, order)));
        }

        recomputeTotals(order);

        try {
            RepairOrder saved = repo.save(order);
            return toResponse(saved);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Concurrent create detected for orderNumber=" + req.orderNumber());
        }
    }

    @Transactional
    public RepairOrderResponse update(Long id, RepairOrderUpdateRequest req) {
        RepairOrder order = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("RepairOrder", id));

        boolean wasOrderAlreadyCompleted = order.getStatus() == RepairOrderStatus.COMPLETED;

        if (req.customerName() != null) order.setCustomerName(req.customerName());
        if (req.customerPhone() != null) order.setCustomerPhone(req.customerPhone());
        if (req.status() != null) order.setStatus(req.status());

        if (req.lineItems() != null) {
            order.getLineItems().clear();
            req.lineItems().forEach(liReq -> order.addLineItem(buildLineItem(liReq, order)));
        }

        recomputeTotals(order);

        try {
            RepairOrder saved = repo.saveAndFlush(order); // we flush so that version is updated immediately and reflected in our response

            // Publish event if status changed to COMPLETED
            if (!wasOrderAlreadyCompleted && saved.getStatus() == RepairOrderStatus.COMPLETED) {
                publisher.publishEvent(new RepairOrderCompletedEvent(saved.getId(), saved.getCustomerName(), Instant.now()));
            }

            return toResponse(saved);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Concurrent update detected for RepairOrder id=" + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        RepairOrder order = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("RepairOrder", id));
        repo.delete(order);
    }

    @Transactional
    public RepairOrderResponse get(Long id) {
        return repo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", id));
    }

    @Transactional
    public PageResponse<RepairOrderResponse> list(Pageable pageable) {
        Page<RepairOrder> page = repo.findAll(pageable);
        List<RepairOrderResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    public PageResponse<RepairOrderResponse> list(Pageable pageable,
                                                  RepairOrderStatus status,
                                                  String vinContains,
                                                  String make,
                                                  String model,
                                                  Instant from,
                                                  Instant to) {
        Specification<RepairOrder> spec =
                RepairOrderSpecifications.hasStatus(status)
                .and(RepairOrderSpecifications.vinContains(vinContains))
                .and(RepairOrderSpecifications.makeEquals(make))
                .and(RepairOrderSpecifications.modelEquals(model))
                .and(RepairOrderSpecifications.createdBetween(from, to));

        Page<RepairOrder> page = repo.findAll(spec, pageable);
        List<RepairOrderResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    public void verifyVersionHeader(Long id, String ifMatchHeader) {
        RepairOrder order = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("RepairOrder", id));
        String currentEtag = "\"" + order.getVersion() + "\"";
        if (!currentEtag.equals(ifMatchHeader)) {
            throw new ConflictException("ETag mismatch. Current=" + currentEtag + " provided=" + ifMatchHeader);
        }
    }

    private RepairLineItem buildLineItem(RepairLineItemRequest item, RepairOrder order) {
        BigDecimal lineTotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
        return RepairLineItem.builder()
                .repairOrder(order)
                .description(item.description())
                .quantity(item.quantity())
                .unitPrice(item.unitPrice())
                .lineTotal(lineTotal)
                .build();
    }

    private void recomputeTotals(RepairOrder order) {
        BigDecimal total = order.getLineItems().stream()
                .map(RepairLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
    }

    private RepairOrderResponse toResponse(RepairOrder order) {
        var items = order.getLineItems().stream().map(li ->
                new RepairLineItemResponse(li.getId(), li.getDescription(), li.getQuantity(), li.getUnitPrice(), li.getLineTotal())
        ).toList();

        return new RepairOrderResponse(
                order.getId(), order.getVersion(),
                order.getOrderNumber(), order.getVin(),
                order.getVehicleYear(), order.getVehicleMake(), order.getVehicleModel(),
                order.getCustomerName(), order.getCustomerPhone(),
                order.getStatus(), order.getTotal(), items
        );
    }
}
