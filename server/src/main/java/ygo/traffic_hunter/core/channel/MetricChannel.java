package ygo.traffic_hunter.core.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import ygo.traffic_hunter.core.channel.collector.MetricCollector;

public class MetricChannel implements MetricProcessingChannel {

    private final MetricCollector collector;

    public MetricChannel(final MetricCollector collector) {
        this.collector = collector;
    }

    @Override
    public void process(final ByteBuffer buffer) {
        collector.collect(buffer);
    }

    @Override
    public void init() {
        collector.registerProcessors();
    }

    @Override
    public void close() throws IOException {
        collector.clear();
    }
}
