package com.interview.repair_order.service;

import com.interview._infrastructure.exceptions.BadRequestException;
import com.interview._infrastructure.exceptions.NotFoundException;
import com.interview.repair_order.api.model.RepairOrderRequest;
import com.interview.repair_order.api.model.RepairOrderResponse;
import com.interview.repair_order.domain.RepairOrder;
import com.interview.repair_order.repository.RepairOrderRepository;
import com.interview.repair_order_line.repository.RepairOrderLineRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class RepairOrderService {

    private static final String NOT_FOUND = "A repair order with ID: %s cannot be found.";
    private RepairOrderRepository repairOrderRepository;
    private RepairOrderLineRepository repairOrderLineRepository;

    public Page<RepairOrderResponse> getAllPaginated(Pageable pageable) {
        //a custom PageResponse would be nicer here
        return repairOrderRepository.findAllWithLinesPageable(pageable).map(RepairOrderResponse::new);
    }

    public RepairOrderResponse getRepairOrder(String id) {
        RepairOrder repairOrder = getRepairOrderFromRepo(id);
        return new RepairOrderResponse(repairOrder);
    }

    @Transactional
    public RepairOrderResponse createRepairOrder(RepairOrderRequest request) {

        //if full tables exist - we would validate the shopId exists

        RepairOrder newRepairOrder = new RepairOrder(request);
        newRepairOrder = repairOrderRepository.save(newRepairOrder);

        return new RepairOrderResponse(newRepairOrder);
    }

    @Transactional
    public RepairOrderResponse updateRepairOrder(String id, RepairOrderRequest repairOrderRequest) {

        RepairOrder repairOrder = getRepairOrderFromRepo(id);

        //this could be a custom validator
        if (repairOrderRequest.getOdometerIn() != null &&
                repairOrderRequest.getOdometerOut() != null &&
                repairOrderRequest.getOdometerIn() > repairOrderRequest.getOdometerOut()) {
            throw new BadRequestException("Odometer In cannot be greater than Odometer Out.");
        }

        repairOrder.setShopId(repairOrderRequest.getShopId());
        repairOrder.setExternalRO(repairOrderRequest.getExternalRO());
        repairOrder.setStatus(repairOrderRequest.getStatus());
        repairOrder.setOdometerIn(repairOrderRequest.getOdometerIn());
        repairOrder.setOdometerOut(repairOrderRequest.getOdometerOut());
        repairOrder.setNotes(repairOrderRequest.getNotes());

        return new RepairOrderResponse(repairOrder);
    }

    @Transactional
    public void deleteRepairOrder(String id) {
        RepairOrder repairOrder = getRepairOrderFromRepo(id);

        repairOrderLineRepository.deleteAll(repairOrder.getRepairOrderLines());
        repairOrder.getRepairOrderLines().clear();
        repairOrderRepository.delete(repairOrder);
    }

    private RepairOrder getRepairOrderFromRepo(String id) {
        return repairOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND, id)));
    }
}
