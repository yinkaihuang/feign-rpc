package cn.bucheng.rpc.config;

import cn.bucheng.rpc.feign.FeignRPCClient;
import cn.bucheng.rpc.proxy.DispatcherServletInherit;
import cn.bucheng.rpc.remoting.RemotingClient;
import cn.bucheng.rpc.remoting.RemotingServer;
import cn.bucheng.rpc.remoting.netty.NettyClientController;
import cn.bucheng.rpc.remoting.netty.NettyRemotingClient;
import cn.bucheng.rpc.remoting.netty.NettyRemotingServer;
import cn.bucheng.rpc.remoting.netty.NettyServerController;
import cn.bucheng.rpc.util.ApplicationUtils;
import cn.bucheng.rpc.util.BeanFactoryUtils;
import com.netflix.loadbalancer.ILoadBalancer;
import feign.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：yinchong
 * @create ：2019/7/8 10:37
 * @description：
 * @modified By：
 * @version:
 */
@Configuration
@ConditionalOnClass(FeignRPCClient.class)
@ConditionalOnProperty(value = "feign.rpc.enable", matchIfMissing = true)
public class FeignRPCConfig {


    @Bean
    @ConditionalOnClass(ILoadBalancer.class)
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory, RemotingClient client) {
        FeignRPCClient feignRPCClient = new FeignRPCClient(client);
        return new LoadBalancerFeignClient(feignRPCClient, cachingFactory, clientFactory);
    }

    @Bean
    @ConditionalOnMissingClass("com.netflix.loadbalancer.ILoadBalancer")
    public Client feignClient(RemotingClient client) {
        //这里使用我们创建的netty客户端
        return new FeignRPCClient(client);
    }

    @Bean
    public RemotingClient remotingClient() {
        return new NettyRemotingClient();
    }

    @Bean
    public RemotingServer remotingServer(DispatcherServletInherit dispatcherServlet) {
        return new NettyRemotingServer(dispatcherServlet);
    }

    @Bean
    public NettyClientController clientController() {
        return new NettyClientController();
    }


    @Bean
    public NettyServerController serverController() {
        return new NettyServerController();
    }


    @Bean
    public BeanFactoryUtils beanFactoryUtils() {
        return new BeanFactoryUtils();
    }

    @Bean
    public ApplicationUtils applicationUtils() {
        return new ApplicationUtils();
    }

}
