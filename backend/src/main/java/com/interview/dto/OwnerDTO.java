package com.interview.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OwnerDTO {

  private Long id;

  private String name;

  private String personalNumber;

  private String address;

  private Instant birthDate;

  private Instant createdAt;

  private Instant updatedAt;
}
