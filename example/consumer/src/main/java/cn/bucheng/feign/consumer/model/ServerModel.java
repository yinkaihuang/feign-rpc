package cn.bucheng.feign.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author ：yinchong
 * @create ：2019/7/25 17:10
 * @description：
 * @modified By：
 * @version:
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ServerModel implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public static ServerModel success(Object data) {
        return new ServerModel(0, "operation ok", data);
    }

    public static ServerModel fail(int code, String message) {
        return new ServerModel(code, message, null);
    }

    public static ServerModel error(String message, Object data) {
        return new ServerModel(-1, message, data);
    }
}
