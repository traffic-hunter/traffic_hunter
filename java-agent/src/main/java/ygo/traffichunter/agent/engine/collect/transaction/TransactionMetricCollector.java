package ygo.traffichunter.agent.engine.collect.transaction;

import java.util.List;
import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.event.consumer.EventConsumer;
import ygo.traffichunter.event.consumer.TransactionEventConsumer;

public class TransactionMetricCollector implements MetricCollector<List<TransactionInfo>> {

    private final EventConsumer<TransactionInfo> consumer = new TransactionEventConsumer();

    @Override
    public List<TransactionInfo> collect() {
        return consumer.pressure();
    }
}
