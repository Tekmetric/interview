package com.interview.dto.estimation;

import com.interview.model.EstimationStatus;

public record EstimationPdfInfo(EstimationStatus estimationStatus, String pdfKey) {
}
