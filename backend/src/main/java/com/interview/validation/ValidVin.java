package com.interview.validation;

import jakarta.validation.constraints.Pattern;

@Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be 17 characters long and contain valid characters")
public @interface ValidVin {
}
