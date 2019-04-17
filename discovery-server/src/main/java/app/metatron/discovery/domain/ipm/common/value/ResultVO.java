package app.metatron.discovery.domain.ipm.common.value;

import java.io.Serializable;

/**
 * API 결과 (공통)
 */

public class ResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 코드
     */
    protected String code;

    /**
     * 메시지
     */
    protected String message;

    /**
     * 데이터
     */
    protected Object data;

    public ResultVO(){}

    public ResultVO(String code, String message){
        this(code, message, null);
    }

    public ResultVO(String code, Object data){
        this(code, "", data);
    }

    public ResultVO(String code, String message, Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
