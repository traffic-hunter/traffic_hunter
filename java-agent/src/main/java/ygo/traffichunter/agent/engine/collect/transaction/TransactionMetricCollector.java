package ygo.traffichunter.agent.engine.collect.transaction;

import ygo.traffichunter.agent.engine.collect.MetricCollector;
import ygo.traffichunter.agent.engine.instrument.collect.TransactionMetric;

public class TransactionMetricCollector implements MetricCollector<TransactionMetric, Void> {

    @Override
    public TransactionMetric collect(final Void conn) {
        return null;
    }
}
