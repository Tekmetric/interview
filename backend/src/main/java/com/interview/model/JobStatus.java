package com.interview.model;

import java.util.Arrays;
import java.util.List;

public enum JobStatus {
    SCHEDULED, IN_PROGRESS, READY_FOR_PICKUP, CANCELLED, COMPLETED;

    public final static List<JobStatus> ALL = Arrays.asList(JobStatus.values());
}
