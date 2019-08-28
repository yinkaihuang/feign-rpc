package cn.bucheng.rpc.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * @author ：yinchong
 * @create ：2019/7/8 17:30
 * @description：
 * @modified By：
 * @version:
 */
public class EnvironmentUtils implements EnvironmentPostProcessor {
    private static Environment environment;

    public static String getValue(String key) {
        return environment.getProperty(key);
    }

    public static Boolean getBooleanValue(String key, boolean defalutValue) {
        String value = environment.getProperty(key);
        if (null == value) {
            return defalutValue;
        }
        return Boolean.parseBoolean(key);
    }

    public static Integer getIntValue(String key, int defaultValue) {
        String value = environment.getProperty(key);
        if (null == value || "".equals(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        EnvironmentUtils.environment = environment;
    }
}
