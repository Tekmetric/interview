package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WishDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private String comment;

    @Size(max = 2048, message = "Link must be less than 2048 characters")
    private String link;

    private boolean cameTrue;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
