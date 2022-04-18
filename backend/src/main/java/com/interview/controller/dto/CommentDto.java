package com.interview.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private LocalDateTime creationDate;

    @Size(max = 1000, message = "Description should have max 1000 chars")
    private String comment;

}
