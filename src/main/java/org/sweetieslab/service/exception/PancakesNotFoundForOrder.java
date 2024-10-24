package org.sweetieslab.service.exception;

public class PancakesNotFoundForOrder extends RuntimeException {

  public PancakesNotFoundForOrder(String message) {
    super(message);
  }
}
