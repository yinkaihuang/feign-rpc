package cn.bucheng.rpc.remoting.netty;

import cn.bucheng.rpc.util.BeanFactoryUtils;
import cn.bucheng.rpc.util.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import cn.bucheng.rpc.constant.FeignRPCConstant;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/7/8 12:58
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class ClientController implements CommandLineRunner {
    @Autowired
    private NettyRemotingClient client;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${server.port}")
    private Integer port;
    private static ScheduledExecutorService loopCheckThread = Executors.newSingleThreadScheduledExecutor();


    @Override
    public void run(String... args) throws Exception {
        initNetty();
        initServerList();
        loopCheckAndLoadRemoteConnection();
    }

    private void initNetty() {
        client.start();
    }

    private void loopCheckAndLoadRemoteConnection() {
        loopCheckThread.scheduleWithFixedDelay(() -> {
            //1.获取并创建新的链接
            List<String> services = discoveryClient.getServices();
            List<String> keys = new LinkedList<>();
            if (null != services) {
                for (String service : services) {
                    if (serviceName.equals(service) || !serverCache.contains(service))
                        continue;
                    List<ServiceInstance> instances = discoveryClient.getInstances(service);
                    if (null != instances) {
                        for (ServiceInstance instance : instances) {
                            String ip = instance.getHost();
                            int port = instance.getPort() + FeignRPCConstant.STEP;
                            keys.add(BaseUtils.createKeyWithIPAndPort(ip, port));
                        }
                    }
                }
            }

            //1.创建最近启动的服务
            for (String key : keys) {
                if (!client.channelActive(key)) {
                    String[] ipAndPorts = key.split("_");
                    client.connect(ipAndPorts[0], Integer.parseInt(ipAndPorts[1]));
                }
            }

            //2.踢去掉失效的链接
            client.removeUnActiveChannel();

        }, 0, 5, TimeUnit.SECONDS);
    }


    private Set<String> serverCache = new HashSet<>();

    //获取当前服务项目的需要创建连接的数量
    private void initServerList() {
        String[] beanNames = BeanFactoryUtils.getBeanFactory().getBeanNamesForAnnotation(FeignClient.class);
        if (beanNames != null && beanNames.length > 0) {
            for (String beanName : beanNames) {
                if (!beanName.contains(".")) {
                    continue;
                }
                Class clazz;
                try {
                    clazz = Class.forName(beanName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    log.error(e.toString());
                    continue;
                }
                FeignClient annotation = (FeignClient) clazz.getAnnotation(FeignClient.class);
                if (annotation == null) {
                    continue;
                }
                String value = annotation.value();
                serverCache.add(value);
            }
        }
    }

}
