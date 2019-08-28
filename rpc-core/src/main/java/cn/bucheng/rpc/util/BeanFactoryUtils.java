package cn.bucheng.rpc.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author ：yinchong
 * @create ：2019/7/5 14:42
 * @description：bean工厂工具类
 * @modified By：
 * @version:
 */
public class BeanFactoryUtils implements BeanFactoryAware {
    static DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanFactoryUtils.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    public static <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return (T) beanFactory.getBean(name);
    }

    public static DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
