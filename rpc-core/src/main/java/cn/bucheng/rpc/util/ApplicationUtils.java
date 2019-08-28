package cn.bucheng.rpc.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ：yinchong
 * @create ：2019/7/5 14:45
 * @description：application工具类
 * @modified By：
 * @version:
 */
public class ApplicationUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationUtils.applicationContext = applicationContext;
    }
}
