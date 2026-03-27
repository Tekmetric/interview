package com.tekmetric;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdates {
  private String email;
  private LocalDate birthDate;
}
