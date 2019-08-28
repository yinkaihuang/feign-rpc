package cn.bucheng.rpc.remoting;

public interface RemotingServer extends RemotingService {
    /**
     * 将服务端绑定到指定端口上面
     * @param port
     */
    void bind(int port);

    /**
     * 判断远程服务端是否正常活跃
     * @return
     */
    boolean isActive();
}
