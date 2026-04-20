package com.interview.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings(value = "EI_EXPOSE_REP",
    justification = "Records are immutable; list contents are not mutated")
public record PageDto<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
