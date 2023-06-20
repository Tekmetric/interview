package com.interview.service.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDm {
    private Long id;
    private String lastname;
    private String firstname;
}
