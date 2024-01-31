package com.interview.dto;

import java.time.Instant;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DataPayload {

    private UUID id;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotNull
    @PositiveOrZero
    @Max(255)
    private Integer count;

    private Instant created;

    private Instant updated;

}
