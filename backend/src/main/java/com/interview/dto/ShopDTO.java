package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name cannot be shorter than 3 chars and longer than 100")
    private String name;

    @NotBlank(message = "Location is mandatory")
    private String location;

    private Set<InvoiceDTO> invoices;

    private Set<SupplierDTO> suppliers;
}
