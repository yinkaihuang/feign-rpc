package cn.bucheng.rpc.remoting.netty;

import cn.bucheng.rpc.remoting.InvokeCallback;
import cn.bucheng.rpc.remoting.protocol.RemotingCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ：yinchong
 * @create ：2019/8/16 14:46
 * @description：
 * @modified By：
 * @version:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ResponseFuture {
    private String xid;
    private long timeoutMillis;
    private InvokeCallback callback;
    private long beginTimestamp = System.currentTimeMillis();
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile RemotingCommand command;
    private volatile boolean sendRequestOK;
    private volatile Throwable cause;
    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    public ResponseFuture(String xid, long timeoutMillis, InvokeCallback callback) {
        this.xid = xid;
        this.timeoutMillis = timeoutMillis;
        this.callback = callback;
    }

    public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.command;
    }

    public void release() {
        try {
            countDownLatch.countDown();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public void putResponse(final RemotingCommand remotingCommand) {
        this.command = remotingCommand;
        this.countDownLatch.countDown();
    }


    public void executeInvokeCallback() {
        if (callback != null) {
            if (executeCallbackOnlyOnce.compareAndSet(false, true)) {
                callback.operationComplete(this);
            }
        }
    }
}
