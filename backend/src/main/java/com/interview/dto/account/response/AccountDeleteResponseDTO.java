package com.interview.dto.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account deletion response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDeleteResponseDTO {
    private Long id;
    private String accountReferenceId;
    private String message;
    private boolean deleted;
}

