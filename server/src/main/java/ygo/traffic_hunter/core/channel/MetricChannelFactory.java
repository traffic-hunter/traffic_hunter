package ygo.traffic_hunter.core.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.channel.collector.MetricCollector;
import ygo.traffic_hunter.core.channel.collector.processor.compress.ByteArrayMetricDecompressor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.repository.MetricRepository;

@Component
@RequiredArgsConstructor
public class MetricChannelFactory {

    private final MetricValidator validator;

    private final ObjectMapper objectMapper;

    private final MetricRepository repository;

    private final ByteArrayMetricDecompressor decompressor;

    public MetricChannel createChannel() {

        final MetricCollector collector = MetricCollector.builder()
                .validator(validator)
                .mapper(objectMapper)
                .repository(repository)
                .decompressor(decompressor)
                .build();

        final MetricChannel channel = new MetricChannel(collector);
        channel.init();

        return channel;
    }
}
