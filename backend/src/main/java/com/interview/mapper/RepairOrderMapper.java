package com.interview.mapper;

import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.model.RepairOrderStatus;
import com.interview.repository.model.RepairOrderEntity;

public class RepairOrderMapper {

    private RepairOrderMapper() {

    }

    public static RepairOrderDto toDto(RepairOrderEntity saved) {
        return RepairOrderDto.builder()
                .id(saved.getId())
                .vin(saved.getVin())
                .carModel(saved.getCarModel())
                .status(saved.getStatus())
                .issueDescription(saved.getIssueDescription())
                .build();
    }

    public static RepairOrderEntity toEntity(CreateRepairOrderRequest createRepairOrderRequest) {
        return RepairOrderEntity.builder()
                .vin(createRepairOrderRequest.vin())
                .carModel(createRepairOrderRequest.carModel())
                .status(RepairOrderStatus.DRAFT)
                .deleted(false)
                .issueDescription(createRepairOrderRequest.issueDescription())
                .build();
    }
}
