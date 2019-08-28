package cn.bucheng.rpc.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author buchengyin
 * @create 2019/8/8 6:35
 * @describe
 */
@Slf4j
public class DispatcherServletInherit extends DispatcherServlet {


    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("remoting rpc invoke");
        super.service(request, response);
    }

}
