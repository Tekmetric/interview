package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.interview.validation.ValidOwnerUpdateRequest;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Data;

@Data
@ValidOwnerUpdateRequest
public class OwnerUpdateRequestDTO {

  @Size(message = "Name cannot exceed 255 characters", max = 255)
  private String name;

  @Size(message = "Personal number cannot exceed 255 characters", max = 255)
  private String personalNumber;

  @Size(message = "Address cannot exceed 500 characters", max = 500)
  private String address;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
  private Instant birthDate;
}
