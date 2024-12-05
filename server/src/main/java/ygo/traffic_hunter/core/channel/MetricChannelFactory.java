package ygo.traffic_hunter.core.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.channel.collector.MetricCollector;

@Component
@RequiredArgsConstructor
public class MetricChannelFactory {

    private final MetricCollector collector;

    public MetricChannel createChannel() {

        final MetricChannel channel = new MetricChannel(collector);
        channel.init();

        return channel;
    }
}
