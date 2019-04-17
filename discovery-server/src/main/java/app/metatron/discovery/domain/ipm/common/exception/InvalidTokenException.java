package app.metatron.discovery.domain.ipm.common.exception;

import static app.metatron.discovery.domain.ipm.common.exception.GlobalErrorCodes.INVALID_TOKEN_CODE;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 인증 오류시 발생
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Authentication failed")
public class InvalidTokenException extends BaseException {

  public InvalidTokenException(String message) {
    super(INVALID_TOKEN_CODE, message);
  }

  public InvalidTokenException(Throwable cause) {
    super(INVALID_TOKEN_CODE, cause);
  }

  public InvalidTokenException(String message, Throwable cause) {
    super(INVALID_TOKEN_CODE, message, cause);
  }
}
