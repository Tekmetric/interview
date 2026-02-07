package com.tekmetric.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.ValidationException;
import org.junit.jupiter.api.Test;

class CarMakeTest {

  @Test
  void fromDisplayName_caseInsensitiveLookup() {
    CarMake make = CarMake.fromDisplayName("toyota");
    assertEquals(CarMake.TOYOTA, make);
  }

  @Test
  void fromDisplayName_unknownMake_throwsValidationException() {
    assertThrows(ValidationException.class, () -> CarMake.fromDisplayName("UnknownBrand"));
  }

  @Test
  void supportsModel_validModel_caseInsensitive() {
    CarMake toyota = CarMake.TOYOTA;
    assertTrue(toyota.supportsModel("RAV4"));
    assertTrue(toyota.supportsModel("rav4")); // case insensitive
  }

  @Test
  void supportsModel_invalidModel_returnsFalse() {
    CarMake toyota = CarMake.TOYOTA;
    assertFalse(toyota.supportsModel("Civic"));
  }
}
