package ygo.traffichunter.event.publisher;

import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.event.handler.TrafficHunterEventHandler;

public class TransactionEventPublisher implements EventPublisher<TransactionInfo> {

    private final TrafficHunterEventHandler<TransactionInfo> eventHandler = new TrafficHunterEventHandler<>();

    @Override
    public void publish(final TransactionInfo event) {
        eventHandler.add(event);
    }
}
