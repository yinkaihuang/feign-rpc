package cn.bucheng.consumer.exception;

import lombok.Getter;

/**
 * @author ：yinchong
 * @create ：2019/7/25 17:52
 * @description：
 * @modified By：
 * @version:
 */
@Getter
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
