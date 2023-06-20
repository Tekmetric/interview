package com.interview.service.model;

import com.interview.repository.model.UserEntity;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDm {
    private Long id;
    private String name;
}
