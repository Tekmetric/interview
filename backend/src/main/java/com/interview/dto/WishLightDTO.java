package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishLightDTO {
    private String name;
    private boolean cameTrue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
