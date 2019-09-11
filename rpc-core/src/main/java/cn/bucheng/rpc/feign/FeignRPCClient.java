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

    /**
     * 将Request对象转变为RemotingCommand对象
     *
     * @param request
     * @param url
     * @param uuid
     * @return
     */
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


    /**
     * 根据uri构建唯一key
     *
     * @param uri
     * @return
     */
    private String createKey(URI uri) {
        int port = uri.getPort() + FeignRPCConstant.STEP;
        return uri.getHost() + UNDERLINE_SYMBOL + port;
    }


    /**
     * 构建唯一ID，用于维持当前调用链
     *
     * @return
     */
    private String uuidKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * 将RemotingCommand转变为Response对象
     *
     * @param response
     * @return
     */
    private Response transformRemotingCommandToResponse(RemotingCommand response) {
        if (null == response) {
            logger.error("not accept response result");
            throw new RuntimeException("not accept response result");
        }
        if (response.getCode() >= 500 && response.getCode() < 600) {
            logger.error("remote invoke error:{}", response.getRemark());
            throw new RuntimeException(response.getError());
        }
        Response.Builder builder = Response.builder().status(response.getCode()).body(response.getBody());
        builder.headers(response.getHeader());
        return builder.build();
    }
}
