package cn.bucheng.rpc.remoting;

public interface RemotingService {
    /**
     * 启动，完成基本的初始化工作。该方法在整个服务启动只会调用一次
     */
    void start();

    /**
     * 关闭，进行一系列的资源释放工作
     */
    void shutdown();

}
