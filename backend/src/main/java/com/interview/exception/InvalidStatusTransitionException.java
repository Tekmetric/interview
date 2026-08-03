package com.interview.exception;

import com.interview.model.RepairOrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(RepairOrderStatus current, RepairOrderStatus target) {
    super("Cannot transition from %s to %s".formatted(current, target));
  }
}
