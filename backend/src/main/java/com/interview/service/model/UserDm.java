package com.interview.service.model;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDm {
    private Long id;
    private String lastname;
    private String firstname;
    private Set<DocumentDm> documents;
}
