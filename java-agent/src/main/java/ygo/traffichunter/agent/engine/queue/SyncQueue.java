package ygo.traffichunter.agent.engine.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;

public enum SyncQueue {

    INSTANCE,
    ;

    private final BlockingQueue<TransactionInfo> syncQ = new LinkedBlockingQueue<>(100);

    public boolean add(final TransactionInfo txInfo) {
        return syncQ.offer(txInfo);
    }

    public TransactionInfo poll() throws InterruptedException {
        return syncQ.take();
    }

    public int size() {
        return syncQ.size();
    }
}
