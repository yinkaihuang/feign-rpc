package cn.bucheng.rpc.feign;

import cn.bucheng.rpc.constant.FeignRPCConstant;
import cn.bucheng.rpc.enu.HandleEnum;
import cn.bucheng.rpc.remoting.RemotingClient;
import cn.bucheng.rpc.remoting.exception.RemotingConnectException;
import cn.bucheng.rpc.remoting.exception.RemotingSendRequestException;
import cn.bucheng.rpc.remoting.exception.RemotingTimeoutException;
import cn.bucheng.rpc.remoting.protocol.RemotingCommand;
import feign.Client;
import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * @author ：yinchong
 * @create ：2019/7/8 10:35
 * @description：
 * @modified By：
 * @version:
 */
public class FeignRPCClient implements Client {

    public static final String UNDERLINE_SYMBOL = "_";
    public static final int REMOTING_RPC_TIMEOUT = 1000 * 60 * 5;
    private static Logger logger = LoggerFactory.getLogger(FeignRPCClient.class);

    private RemotingClient remotingClient;

    public FeignRPCClient(RemotingClient client) {
        this.remotingClient = client;
    }
    
    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        String url = request.url();
        URI uri = URI.create(url);
        String key = createKey(uri);
        String uuid = uuidKey();
        RemotingCommand requestCommand = transformRequestToRemotingCommand(request, url, uuid);
        RemotingCommand remotingCommand = null;
        try {
            remotingCommand = remotingClient.invokeSync(key, requestCommand, REMOTING_RPC_TIMEOUT);
            return transformRemotingCommandToResponse(remotingCommand);
        } catch (RemotingSendRequestException e) {
            logger.error(e.toString());
            throw new RuntimeException("远程发送失败:" + e.toString());
        } catch (RemotingTimeoutException e) {
            logger.error(e.toString());
            throw new RuntimeException("远程发送超时:" + e.toString());
        } catch (RemotingConnectException e) {
            logger.error(e.toString());
            throw new RuntimeException("远程连接异常:" + e.toString());
        } catch (Throwable throwable) {
            logger.error(throwable.toString());
            throw new RuntimeException(throwable.toString());
        }
    }

    private RemotingCommand transformRequestToRemotingCommand(Request request, String url, String uuid) {
        RemotingCommand requestCommand = new RemotingCommand();
        requestCommand.setXid(uuid);
        requestCommand.setBody(request.body());
        requestCommand.setMethod(request.method());
        requestCommand.setUrl(url);
        requestCommand.setHeader(request.headers());
        requestCommand.setType(HandleEnum.REQUEST_COMMAND.getCode());
        return requestCommand;
    }


    private String createKey(URI uri) {
        Integer port = uri.getPort() + FeignRPCConstant.STEP;
        String temp = uri.getHost() + UNDERLINE_SYMBOL + port;
        return temp;
    }


    private String uuidKey() {
        return UUID.randomUUID().toString();
    }

    private Response transformRemotingCommandToResponse(RemotingCommand response) {
        if (null == response) {
            logger.error("not accept response result");
            throw new RuntimeException("not accept response result");
        }
        if (response.getCode() >= 500 && response.getCode() < 600) {
            logger.error("remote invoke error:{}", response.getRemark());
            Response.Builder builder = Response.builder().status(response.getCode()).body(response.getError().getBytes());
            builder.headers(response.getHeader());
            return builder.build();
        }
        Response.Builder builder = Response.builder().status(response.getCode()).body(response.getBody());
        builder.headers(response.getHeader());
        return builder.build();
    }
}
