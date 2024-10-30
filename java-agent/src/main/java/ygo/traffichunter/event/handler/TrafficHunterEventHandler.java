package ygo.traffichunter.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrafficHunterEventHandler<M> implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TrafficHunterEventHandler.class);

    private static final int QUEUE_CAPACITY = 200;

    private final BlockingQueue<M> eventQ = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    public boolean add(M metric) {
        try {
            return eventQ.offer(metric);
        } catch (Exception e) {
            log.error("event handler error (add) = {}", e.getMessage());
            return false;
        }
    }

    public M poll() throws InterruptedException {
        return eventQ.take();
    }

    public List<M> drain() {
        List<M> list = new ArrayList<>(eventQ.size());
        eventQ.drainTo(list, eventQ.size());

        return list;
    }

    public M peek() {
        return eventQ.peek();
    }

    public boolean isEmpty() {
        return eventQ.isEmpty();
    }

    public int size() {
        return eventQ.size();
    }

    @Override
    public void run() {
        log.info("shut down event handler");
        eventQ.clear();
    }
}
