package ygo.traffichunter.event.consumer;

import java.util.List;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.event.handler.TrafficHunterEventHandler;

public class TransactionEventConsumer implements EventConsumer<TransactionInfo> {

    private final TrafficHunterEventHandler<TransactionInfo> eventHandler = new TrafficHunterEventHandler<>();

    @Override
    public TransactionInfo consume() {
        try {
            return eventHandler.poll();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public List<TransactionInfo> pressure() {
        return eventHandler.drain();
    }
}
