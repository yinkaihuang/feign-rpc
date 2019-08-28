package cn.bucheng.rpc.util;

/**
 * @author ：yinchong
 * @create ：2019/7/8 15:28
 * @description：
 * @modified By：
 * @version:
 */
public class BaseUtils {

    public static String createKeyWithIPAndPort(String ip,int port){
        return ip+"_"+port;
    }
}
