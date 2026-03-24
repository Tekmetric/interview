package com.interview.dto.response;

import lombok.Builder;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class MechanicShopResponse {

    private Long id;
    private String shopName;
    private String phoneNumber;
    private String email;
    private Set<MechanicResponse> mechanicsResponse;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
}
