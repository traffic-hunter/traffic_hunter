package ygo.traffichunter.agent.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traffic-hunter event handler
 * sync based.
 */
public class TrafficHunterEvnetHandler<M> {

    private static final Logger log = LoggerFactory.getLogger(TrafficHunterEvnetHandler.class);

    private static final int MAX_CAPACITY = 500;

    private final BlockingQueue<M> eventQueue = new LinkedBlockingQueue<>(MAX_CAPACITY);

    public boolean add(M metric) {
        try {
            return eventQueue.offer(metric);
        } catch (Exception e) {
            log.error("event handler error (add) = {}", e.getMessage());
            return false;
        }
    }

    public M poll() throws InterruptedException {
        return eventQueue.take();
    }

    public List<M> drain() {
        List<M> list = new ArrayList<>(eventQueue.size());
        eventQueue.drainTo(list, eventQueue.size());

        return list;
    }

    public M peek() {
        return eventQueue.peek();
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

    public int size() {
        return eventQueue.size();
    }
}
