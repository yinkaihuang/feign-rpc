package cn.bucheng.rpc.remoting.netty;

import cn.bucheng.rpc.enu.HandleEnum;
import cn.bucheng.rpc.remoting.exception.RemotingSendRequestException;
import cn.bucheng.rpc.remoting.exception.RemotingTimeoutException;
import cn.bucheng.rpc.remoting.protocol.RemotingCommand;
import com.alibaba.fastjson.JSON;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这里主要的成员变量为key 和远程通道
 *
 * @author ：yinchong
 * @create ：2019/8/16 14:45
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public abstract class AbstractNettyRemoting {

    //存放唯一标示(UUID)和结果
    protected final ConcurrentHashMap<String, ResponseFuture> responseTable = new ConcurrentHashMap<>();

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand cmd) {
        if (cmd.getType() == HandleEnum.REQUEST_COMMAND.getCode()) {
            processRequestCommand(ctx, cmd);
        } else if (cmd.getType() == HandleEnum.RESPONSE_COMMAND.getCode()) {
            processResponseCommand(ctx, cmd);
        } else if (cmd.getType() == HandleEnum.PING_COMMAND.getCode()) {
            log.debug("receive ping from remoting client");
        }
    }

    /**
     * 这个方法用于服务端实现
     *
     * @param ctx
     * @param cmd
     */
    public void processRequestCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {

    }

    public void processResponseCommand(final ChannelHandlerContext ctx, final RemotingCommand cmd) {
        String xid = cmd.getXid();
        ResponseFuture responseFuture = responseTable.get(xid);
        responseFuture.putResponse(cmd);
    }

    public RemotingCommand invokeSyncImpl(final Channel channel, final RemotingCommand request, final long timeoutMillis) throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException {
        String xid = request.getXid();
        try {
            final SocketAddress addr = channel.remoteAddress();
            ResponseFuture responseFuture = new ResponseFuture(xid, timeoutMillis, null);
            responseTable.put(xid, responseFuture);
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    }
                    responseFuture.setSendRequestOK(false);
                    responseFuture.setCause(future.cause());
                    responseFuture.putResponse(null);
                }
            });
            RemotingCommand responseCommand = responseFuture.waitResponse(timeoutMillis);
            if (null == responseCommand) {
                if (responseFuture.isSendRequestOK()) {
                    throw new RemotingTimeoutException(addr.toString(), timeoutMillis,
                            responseFuture.getCause());
                } else {
                    throw new RemotingSendRequestException(addr.toString(), responseFuture.getCause());
                }
            }
            return responseCommand;
        } finally {
            responseTable.remove(xid);
        }

    }


    /**
     * 扫描结果列表，将超时的结果释放
     */
    protected void scanResponseTable() {
        final List<ResponseFuture> rfList = new LinkedList<>();
        Iterator<Map.Entry<String, ResponseFuture>> iterator = this.responseTable.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ResponseFuture> next = iterator.next();
            ResponseFuture rep = next.getValue();
            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {
                rep.release();
                iterator.remove();
                rfList.add(rep);
            }
        }

        for (ResponseFuture rf : rfList) {
            executeInvokeCallback(rf);
        }
    }


    private void executeInvokeCallback(ResponseFuture rf) {
        rf.executeInvokeCallback();
    }

    protected class RemotingCommandHandle extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            ctx.writeAndFlush(JSON.toJSONString(msg));
        }

    }
}
