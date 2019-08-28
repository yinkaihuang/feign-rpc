package cn.bucheng.feign.consumer.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BusinessErrorEnum {
    NO_FIND_ROUTING(500, "没有找到访问路径"),
    BIND_PARAM_FAIL(5001, "参数绑定异常"),
    PARAM_VERIFY_FAIL(5002, "参数校验失败"),
    ;
    private int code;
    private String message;
}
