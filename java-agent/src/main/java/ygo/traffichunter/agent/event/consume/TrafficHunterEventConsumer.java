package ygo.traffichunter.agent.event.consume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.event.TrafficHunterEvnetHandler;

public class TrafficHunterEventConsumer<T> implements EventConsumer<T> {

    private static final Logger log = LoggerFactory.getLogger(TrafficHunterEventConsumer.class);

    private final TrafficHunterEvnetHandler<T> evnetHandler = new TrafficHunterEvnetHandler<>();

    @Override
    public T consume() {
        try {
            return evnetHandler.poll();
        } catch (InterruptedException e) {
            log.error("event consumer interrupted = {}", e.getMessage());
            return null;
        }
    }
}
