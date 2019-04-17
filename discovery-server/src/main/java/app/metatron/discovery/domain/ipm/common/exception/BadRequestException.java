package app.metatron.discovery.domain.ipm.common.exception;

import static app.metatron.discovery.domain.ipm.common.exception.GlobalErrorCodes.BAD_REQUEST_CODE;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 접근 거부 오류시 발생 (Role처리 오류)
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class BadRequestException extends BaseException {

    BindingResult bindingResult;

    public BadRequestException(String message) {
        super(BAD_REQUEST_CODE, message);
    }

    public BadRequestException(BindingResult bindingResult) {
        super(BAD_REQUEST_CODE, bindingResult.toString());
        this.bindingResult = bindingResult;
    }

    public BadRequestException(Throwable cause) {
        super(BAD_REQUEST_CODE, cause);
    }

    public BadRequestException(String message, Throwable cause) {
        super(BAD_REQUEST_CODE, message, cause);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
