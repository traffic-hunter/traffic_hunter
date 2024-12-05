package ygo.traffic_hunter.core.channel.collector;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;

@Component
@RequiredArgsConstructor
public class MetricCollector {

    private final Set<MetricHandler> handlers;

    public void collect(final ByteBuffer byteBuffer) {
        if(handlers.isEmpty()) {
            throw new IllegalStateException("collector is empty..");
        }

        byte[] data = convert(byteBuffer);

        byte header = data[0];

        MetricHandler metricHandler = handlers.stream()
                .filter(handler -> Objects.equals(handler.getHeader(), header))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Support Handler.."));

        metricHandler.handle(data);
    }

    public void clear() {
        handlers.clear();
    }

    private byte[] convert(final ByteBuffer byteBuffer) {
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        return data;
    }
}
