package ygo.traffic_hunter.core.collector;

import java.nio.ByteBuffer;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Collector;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;

@Collector
@RequiredArgsConstructor
public class MetricCollector {

    private final Set<MetricChannel> handlers;

    public void collect(final ByteBuffer byteBuffer) {
        if(handlers.isEmpty()) {
            throw new IllegalStateException("collector is empty..");
        }

        byte[] data = convert(byteBuffer);

        byte header = data[0];

        MetricChannel metricChannel = handlers.stream()
                .filter(handler -> handler.getHeaderSpec().equals(header))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Support Handler.."));

        metricChannel.open(data);
    }

    private byte[] convert(final ByteBuffer byteBuffer) {
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        return data;
    }
}
