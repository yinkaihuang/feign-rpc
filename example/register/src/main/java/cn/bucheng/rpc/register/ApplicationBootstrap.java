package cn.bucheng.rpc.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author buchengyin
 * @create 2019/8/12 19:14
 * @describe
 */
@SpringBootApplication
@EnableEurekaServer
public class ApplicationBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }

}
