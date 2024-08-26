package xyz.telegram.depinalliance.common.exceptions;

/**
 * @author holden on 25-Jul-2024
 */
public class AccessDeniedException extends RuntimeException {

  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }
}
