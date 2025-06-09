package com.interview.dto.owner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.interview.validation.ValidOwnerUpdateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Schema(
    name = "OwnerUpdateRequestDTO",
    description =
        "Request payload for updating an existing owner. "
            + "This acts in a patch manner as well, so that fields that are not specified will not be updated.")
@Data
@ValidOwnerUpdateRequest
public class OwnerUpdateRequestDTO {

  @Schema(description = "Name of the owner", example = "John Doe")
  @Size(message = "Name cannot exceed 255 characters", max = 255)
  private String name;

  @Schema(description = "Personal number of the owner", example = "1234567890")
  @Size(message = "Personal number cannot exceed 255 characters", max = 255)
  private String personalNumber;

  @Schema(description = "Address of the owner", example = "Some Street 123, City, Country")
  @Size(message = "Address cannot exceed 500 characters", max = 500)
  private String address;

  @Schema(
      description = "The birth date of the owner in ISO 8601 format",
      example = "1990-01-01T00:00:00Z")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
  private Instant birthDate;

  @Schema(description = "List of car IDs associated with the owner", example = "[1, 2, 3]")
  private List<Long> carIds;
}
