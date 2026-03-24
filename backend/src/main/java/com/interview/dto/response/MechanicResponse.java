package com.interview.dto.response;

import com.interview.model.Mechanic;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class MechanicResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Mechanic.Role role;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
}
