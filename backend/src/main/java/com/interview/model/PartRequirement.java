package com.interview.model;

import lombok.*;
import lombok.experimental.Accessors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Accessors(chain = true)
public class PartRequirement {
    private Long partId;
    private int requiredCount;
}
