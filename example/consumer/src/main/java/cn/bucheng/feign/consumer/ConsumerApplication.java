package cn.bucheng.feign.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ConsumerApplication {


    public static void main(String[] args) {
        int number = Runtime.getRuntime().availableProcessors() * 2;
        System.out.println(number);
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
