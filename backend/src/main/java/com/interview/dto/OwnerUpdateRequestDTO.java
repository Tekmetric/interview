package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.interview.validation.ValidOwnerUpdateRequest;
import java.time.Instant;
import lombok.Data;

@Data
@ValidOwnerUpdateRequest
public class OwnerUpdateRequestDTO {

  private String name;

  private String personalNumber;

  private String address;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
  private Instant birthDate;
}
