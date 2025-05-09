package application_operation.ParkFlow.exception;

import lombok.Getter;

@Getter
public class HandleException extends RuntimeException {
  private final String code;

  public HandleException(String code, String message) {
    super(message);
    this.code = code;
  }
}
