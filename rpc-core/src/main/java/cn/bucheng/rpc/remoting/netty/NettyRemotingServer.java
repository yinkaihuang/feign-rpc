package cn.bucheng.rpc.remoting.netty;

import cn.bucheng.rpc.enu.HandleEnum;
import cn.bucheng.rpc.mock.MockHttpServletRequest;
import cn.bucheng.rpc.mock.MockHttpServletResponse;
import cn.bucheng.rpc.proxy.DispatcherServletInherit;
import cn.bucheng.rpc.remoting.RemotingServer;
import cn.bucheng.rpc.remoting.protocol.RemotingCommand;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：yinchong
 * @create ：2019/8/19 9:14
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class NettyRemotingServer extends AbstractNettyRemoting implements RemotingServer {
    private ServerBootstrap bootstrap;
    private volatile Channel serverChannel;
    private NioEventLoopGroup workGroup;
    private NioEventLoopGroup bossGroup;
    private AtomicInteger workThreadIndex = new AtomicInteger(0);
    private ThreadPoolExecutor poolExecutor;
    private AtomicInteger serverHandleThreadIndex = new AtomicInteger(0);
    private Timer timer = new Timer("response_future_timer", true);
    @Autowired
    private DispatcherServletInherit dispatcherServlet;

    public NettyRemotingServer(DispatcherServletInherit dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @SuppressWarnings("all")
    @Override
    public void start() {
        TaskQueue queue = new TaskQueue(10000);
        poolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 10, 10, TimeUnit.SECONDS, queue, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "rpc_server_handle_thread_" + serverHandleThreadIndex.getAndIncrement());
            }
        });
        queue.setParent(poolExecutor);
        workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "netty_server_work_thread_" + workThreadIndex.getAndIncrement());
            }
        });
        bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "netty_server_accept_thread");
            }
        });
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 1024 * 100)
                .childOption(ChannelOption.SO_SNDBUF, 1024 * 100)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 8, 0, 8));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new RemotingServerHandler());
                        ch.pipeline().addLast(new RemotingCommandHandle());
                        ch.pipeline().addFirst(new StringEncoder());
                        ch.pipeline().addFirst(new LengthFieldPrepender(8));
                    }
                });
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    //扫描结果响应表
                    scanResponseTable();
                } catch (Exception e) {
                    log.error("scanResponseTable exception", e);
                }
            }
        }, 1000 * 3, 1000 * 10);
    }

    @Override
    public void shutdown() {
        timer.cancel();
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (poolExecutor != null) {
            poolExecutor.shutdown();
        }
    }

    @Override
    public boolean isActive() {
        if (serverChannel == null || !serverChannel.isActive())
            return false;
        return true;
    }


    @Override
    public void bind(int port) {
        try {
            bootstrap.bind(port).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        serverChannel = future.channel();
                        log.info("netty server bind port:{} success", port);
                        return;
                    }
                    log.warn("netty server in port:{} fail,cause:{}", port, future.cause());
                    serverChannel = null;
                }
            });
        } catch (InterruptedException e) {
            log.warn("netty server start fail,bind {} fail,cause:{}", port, e);
        }
    }

    @Override
    public void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand cmd) {
        try {
            poolExecutor.execute(() -> {
                handleMessage(ctx, cmd);
            });
        } catch (RejectedExecutionException e) {
            log.error("to many remoting connection");
            RemotingCommand rp = new RemotingCommand();
            rp.setType(HandleEnum.RESPONSE_COMMAND.getCode());
            rp.setXid(cmd.getXid());
            rp.setCode(500);
            rp.setError("to many remoting connection ,reject remoting connect");
            ctx.pipeline().writeAndFlush(rp);
        } catch (Throwable throwable) {
            log.error(throwable.toString());
            RemotingCommand rp = new RemotingCommand();
            rp.setType(HandleEnum.RESPONSE_COMMAND.getCode());
            rp.setXid(cmd.getXid());
            rp.setCode(500);
            rp.setError(throwable.toString());
            ctx.pipeline().writeAndFlush(rp);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, RemotingCommand cmd) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String xid = cmd.getXid();
        RemotingCommand rp = new RemotingCommand();
        rp.setXid(xid);
        rp.setType(HandleEnum.RESPONSE_COMMAND.getCode());
        transformRemotingCommandToRequest(cmd, request);
        try {
            dispatcherServlet.service(request, response);
            transformResponseToRemotingCommand(response, rp);
        } catch (ServletException e) {
            log.error(e.toString());
            transformThrowableToRemotingCommand(response, rp, 500, e.toString());
        } catch (IOException e) {
            log.error(e.toString());
            transformThrowableToRemotingCommand(response, rp, 500, e.toString());
        } catch (Throwable throwable) {
            log.error(throwable.toString());
            transformThrowableToRemotingCommand(response, rp, 500, throwable.toString());
        }
        ctx.pipeline().writeAndFlush(rp);
    }


    private void transformRemotingCommandToRequest(RemotingCommand rq, MockHttpServletRequest request) {
        recodeHeadersToRequest(rq, request);
        String method = rq.getMethod();
        URI uri = URI.create(rq.getUrl());
        request.setMethod(method);
        request.setScheme(uri.getScheme());
        request.setContent(rq.getBody());
        request.setRequestURI(uri.getPath());
        request.setServletPath(uri.getPath());
        request.setContextPath(uri.getPath());
        if (!StringUtils.isEmpty(uri.getQuery())) {
            request.setParameters(transformQueryToMap(uri.getQuery()));
        }
    }


    private void recodeHeadersToRequest(RemotingCommand rq, MockHttpServletRequest request) {
        Map<String, Collection<String>> headers = rq.getHeader();
        if (null != headers) {
            for (String key : headers.keySet()) {
                Collection<String> values = headers.get(key);
                if (values != null) {
                    for (Object value : values) {
                        request.addHeader(key, value);
                    }
                }
            }
        }
    }


    private Map<String, String> transformQueryToMap(String query) {
        String[] temps = query.split("&");
        if (null == temps) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String temp : temps) {
            String[] split = temp.split("=");
            result.put(split[0], split[1]);
        }
        return result;
    }

    private void transformResponseToRemotingCommand(MockHttpServletResponse response, RemotingCommand rp) {
        rp.setBody(response.getContentAsByteArray());
        rp.setCode(response.getStatus());
        addHeaderToRemotingCommand(response, rp);
    }

    private void transformThrowableToRemotingCommand(MockHttpServletResponse response, RemotingCommand rp, int errorCode, String errorMsg) {
        rp.setCode(errorCode);
        rp.setError(errorMsg);
        addHeaderToRemotingCommand(response, rp);
    }

    private void addHeaderToRemotingCommand(MockHttpServletResponse response, RemotingCommand rp) {
        Collection<String> headerNames = response.getHeaderNames();
        if (null != headerNames) {
            Map<String, Collection<String>> headers = new HashMap<>();
            for (String headerName : headerNames) {
                headers.put(headerName, response.getHeaders(headerName));
            }
            rp.setHeader(headers);
        }
    }

    @SuppressWarnings("all")
    private class RemotingServerHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error(cause.toString());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            RemotingCommand remotingCommand = JSON.parseObject(msg, RemotingCommand.class);
            processMessageReceived(ctx, remotingCommand);
        }
    }

}
