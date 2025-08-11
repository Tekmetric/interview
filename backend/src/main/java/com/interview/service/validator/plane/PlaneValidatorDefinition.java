package com.interview.service.validator.plane;

/**
 * Groups command records used by plane business validators.
 */
public interface PlaneValidatorDefinition {

    record Exist(int planeId) {}
}
