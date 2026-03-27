package com.tekmetric.response.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.tekmetric.response.Visibility;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  @JsonView(Visibility.Public.class)
  private UUID id;

  @JsonView(Visibility.Public.class)
  private String firstName;

  @JsonView(Visibility.Public.class)
  private String lastName;

  @JsonView(Visibility.Detailed.class)
  private String email;

  @JsonView(Visibility.Detailed.class)
  private LocalDate birthDate;
}
