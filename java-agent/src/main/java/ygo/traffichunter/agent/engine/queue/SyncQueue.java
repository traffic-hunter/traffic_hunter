package ygo.traffichunter.agent.engine.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import ygo.traffichunter.agent.engine.metric.transaction.TransactionInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public enum SyncQueue {

    INSTANCE,
    ;

    private final BlockingQueue<TransactionInfo> syncQ = new LinkedBlockingQueue<>(100);

    /**
     * this method is non-blocking
     * @param txInfo
     * @return success : true, fail : false
     */
    public boolean add(final TransactionInfo txInfo) {
        return syncQ.offer(txInfo);
    }

    /**
     * this method is blocking
     * @return txInfo
     * @throws InterruptedException
     */
    public TransactionInfo poll() throws InterruptedException {
        return syncQ.take();
    }

    public void removeAll() {
        syncQ.clear();
    }

    public int size() {
        return syncQ.size();
    }
}
