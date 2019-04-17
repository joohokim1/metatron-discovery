package app.metatron.discovery.domain.ipm.common.exception;

import com.google.common.base.Preconditions;
import org.springframework.validation.BindingResult;

/*
 * Class Name : BaseException
 * 
 * Class Description: BaseException Class
 *
 * Created by nogah on 2018-03-05.
 *
 * Version : v1.0
 *
 */
public class BaseException extends RuntimeException {

    ErrorCodes code;
    BindingResult bindingResult;


    public BaseException(ErrorCodes code, String message) {
        this(code, message, null);
    }

    public BaseException(ErrorCodes code, Throwable cause) {
        this(code, cause.getMessage(), cause);
    }

    public BaseException(ErrorCodes code, String message, Throwable cause) {
        super(message, cause);
        this.code = Preconditions.checkNotNull(code);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public ErrorCodes getCode() {
        return code;
    }
}
