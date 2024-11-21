package ygo.traffic_hunter.core.channel.collector.handler;

import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.repository.MetricRepository;

public interface MetricHandler {

    void handle(byte[] payload, MetricValidator validator, MetricRepository repository);
}
