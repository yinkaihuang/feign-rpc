package cn.bucheng.rpc.remoting.netty;

import cn.bucheng.rpc.constant.FeignRPCConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/7/5 14:48
 * @description：上下文中url地址映射刷新
 * @modified By：
 * @version:
 */
@Order(-Integer.MAX_VALUE)
public class NettyServerController implements CommandLineRunner {
    @Autowired
    private NettyRemotingServer server;
    @Value("${server.port}")
    private int port;

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void run(String... args) throws Exception {
        initServer();
        startServer();
    }

    private void initServer() {
        server.start();
    }


    //启动
    private void startServer() {
        //判断server是否正常如果不正常则再次绑定端口
        executor.scheduleWithFixedDelay(() -> {
                    if (!server.isActive()) {
                        server.bind(port + FeignRPCConstant.STEP);
                    }
                }
                , 0, 5, TimeUnit.SECONDS);
    }

}
