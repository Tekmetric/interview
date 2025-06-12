package com.interview.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * PaginationRequestDTO is used to encapsulate pagination parameters for API requests so that they can be validated properly.
 * It includes fields for page number, page size, sorting criteria, and default values.
 */
@Getter
@Setter
public class PaginationRequestDTO {

  @NotNull
  @Min(0)
  private Integer page = 0;

  @NotNull
  @Min(1)
  private Integer size = 10;

  private String sortBy = "id";
  private String sortDir = "asc";

}
