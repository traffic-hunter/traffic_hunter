package ygo.traffic_hunter.core.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import ygo.traffic_hunter.core.channel.collector.MetricCollector;

@Slf4j
public class MetricChannel implements MetricProcessingChannel {

    private final MetricCollector collector;

    public MetricChannel(final MetricCollector collector) {
        this.collector = collector;
    }

    @Override
    public void init() {
        log.info("MetricChannel init = {} {}", collector.getClass().getSimpleName(), collector.hashCode());
    }

    @Override
    public void process(final ByteBuffer buffer) {
        collector.collect(buffer);
    }

    @Override
    public void close() throws IOException {
        collector.clear();
    }
}
