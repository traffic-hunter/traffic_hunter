package ygo.traffichunter.agent.engine.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import ygo.traffichunter.agent.engine.metric.transaction.TraceInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public enum SyncQueue {

    INSTANCE,
    ;

    private final BlockingQueue<TraceInfo> syncQ = new LinkedBlockingQueue<>(100);

    /**
     * this method is non-blocking
     * @return success : true, fail : false
     */
    public boolean add(final TraceInfo trInfo) {
        return syncQ.offer(trInfo);
    }

    /**
     * this method is blocking
     */
    public TraceInfo poll() throws InterruptedException {
        return syncQ.take();
    }

    public void removeAll() {
        syncQ.clear();
    }

    public int size() {
        return syncQ.size();
    }
}
