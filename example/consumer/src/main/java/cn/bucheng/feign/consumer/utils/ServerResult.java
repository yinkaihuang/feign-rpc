package cn.bucheng.feign.consumer.utils;

import java.io.Serializable;

/**
 * @author buchengyin
 * @Date 2019/3/30 15:29
 **/
public class ServerResult implements Serializable {
    public static int FAIL = 500;
    public static int SUCCESS = 200;
    private int code;
    private String message;
    private Object data;

    public ServerResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ServerResult fail(String message) {
        return new ServerResult(FAIL, message, null);
    }

    public static ServerResult success(String message) {
        return new ServerResult(SUCCESS, message, null);
    }

    public static ServerResult success() {
        return success("operation success");
    }

    public static int getFAIL() {
        return FAIL;
    }

    public static void setFAIL(int FAIL) {
        ServerResult.FAIL = FAIL;
    }

    public static int getSUCCESS() {
        return SUCCESS;
    }

    public static void setSUCCESS(int SUCCESS) {
        ServerResult.SUCCESS = SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public static ServerResult successWithData(Object data) {
        return new ServerResult(SUCCESS, "operation ok", data);
    }
}
