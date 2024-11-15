package ygo.traffic_hunter.core.concurrency;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;/**
 *
 */
public class SyncQueueManager<T> {

    private final Queue<T> queue;

    public SyncQueueManager(final Integer capacity) {
        this.queue = new LinkedBlockingQueue<>(Objects.requireNonNullElse(capacity, 100));
    }

    public void add(final T systemInfo) {
        queue.add(systemInfo);
    }

    public T poll() {
        return queue.poll();
    }
}
