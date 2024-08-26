package xyz.telegram.depinalliance.common.exceptions;

import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;

/**
 * @author holden on 25-Jul-2024
 */
public class BusinessException extends Exception {
  private long error;

  public BusinessException(String message) {
    super(message);
  }

  public BusinessException() {
    super(ResponseMessageConstants.DATA_INVALID);
  }

  public BusinessException(long error) {
    super(ResponseMessageConstants.DATA_INVALID);
    setError(error);
  }

  public BusinessException(String message, long error) {
    super(message);
    setError(error);
  }

  public long getError() {
    return error;
  }

  public void setError(long error) {
    this.error = error;
  }
}
