package ygo.traffichunter.agent.event.publisher;

import ygo.traffichunter.agent.event.TrafficHunterEvnetHandler;

public class TrafficHunterEventPublisher<T> implements EventPublisher<T> {

    private final TrafficHunterEvnetHandler<T> evnetHandler = new TrafficHunterEvnetHandler<>();

    @Override
    public void publish(final T obj) {
        evnetHandler.add(obj);
    }
}
