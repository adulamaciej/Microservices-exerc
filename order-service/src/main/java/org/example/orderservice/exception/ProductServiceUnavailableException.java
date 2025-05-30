package org.example.orderservice.exception;

public class ProductServiceUnavailableException extends RuntimeException {
  public ProductServiceUnavailableException(String message) {
    super(message);
  }
}
