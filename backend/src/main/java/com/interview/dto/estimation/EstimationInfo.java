package com.interview.dto.estimation;

import com.interview.dto.workitem.WorkItemDto;

import java.util.List;

public record EstimationInfo(String carVin, List<WorkItemDto> workItems) {
}
