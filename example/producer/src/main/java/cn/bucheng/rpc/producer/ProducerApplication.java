package cn.bucheng.rpc.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author buchengyin
 * @create 2019/8/12 19:26
 * @describe
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}
