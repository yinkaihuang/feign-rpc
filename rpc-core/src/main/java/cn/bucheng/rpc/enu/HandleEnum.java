package cn.bucheng.rpc.enu;

import lombok.Data;
import lombok.Setter;

/**
 * @author ：yinchong
 * @create ：2019/8/16 15:32
 * @description：
 * @modified By：
 * @version:
 */
public enum HandleEnum {
    REQUEST_COMMAND(1),
    RESPONSE_COMMAND(2),
    PING_COMMAND(0);

    private int code;

    HandleEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }}
