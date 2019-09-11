package cn.bucheng.rpc.autoconfiguration;

import cn.bucheng.rpc.proxy.DispatcherServletInherit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletRegistration;

import static org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME;

/**
 * @author buchengyin
 * @create 2019/8/8 8:30
 * @describe
 */
@Configuration
@ConditionalOnProperty(value = "feign.rpc.enable", matchIfMissing = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(DispatcherServletInherit.class)
public class DispatcherServletProxyAutoConfiguration {

    @Configuration
    @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnClass(ServletRegistration.class)
    @EnableConfigurationProperties(WebMvcProperties.class)
    protected static class DispatcherServletConfiguration {

        private final WebMvcProperties webMvcProperties;

        public DispatcherServletConfiguration(WebMvcProperties webMvcProperties) {
            this.webMvcProperties = webMvcProperties;
        }

        /**
         * 向spring ioc容器中注入代理后的DispatcherServlet对象
         * @return
         */
        @Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
        @Primary
        public DispatcherServlet dispatcherServlet() {
            DispatcherServlet dispatcherServlet = new DispatcherServletInherit();
            dispatcherServlet.setDispatchOptionsRequest(
                    this.webMvcProperties.isDispatchOptionsRequest());
            dispatcherServlet.setDispatchTraceRequest(
                    this.webMvcProperties.isDispatchTraceRequest());
            dispatcherServlet.setThrowExceptionIfNoHandlerFound(
                    this.webMvcProperties.isThrowExceptionIfNoHandlerFound());
            return dispatcherServlet;
        }
    }

    /**
     * 动态修改DispatcherServletRegistration中的属性的值loadOnStartup为1
     * 表示DispatcherServlet的刷新为对象创建时而不是延迟到第一次http请求方法进行
     * @return
     */
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(
                    ConfigurableListableBeanFactory beanFactory) throws BeansException {
                BeanDefinition bean = beanFactory.getBeanDefinition(
                        DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
                bean.getPropertyValues().add("loadOnStartup", 1);
            }
        };
    }
}
