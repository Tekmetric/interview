package com.interview.dto.estimation;

import com.interview.model.EstimationStatus;

public record EstimationDto(
        EstimationStatus estimationStatus,
        String pdfUrl

) {

    public EstimationDto(EstimationStatus estimationStatus) {
        this(estimationStatus, null);
    }
}
