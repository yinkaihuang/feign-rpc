package cn.bucheng.rpc.remoting;

import cn.bucheng.rpc.remoting.netty.ResponseFuture;

public interface InvokeCallback {
    void operationComplete(ResponseFuture rf);
}
