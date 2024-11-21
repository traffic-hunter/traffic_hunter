package ygo.traffic_hunter.core.channel.collector.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import ygo.traffic_hunter.core.annotation.Processor;
import ygo.traffic_hunter.core.channel.collector.processor.compress.ByteArrayMetricDecompressor;
import ygo.traffic_hunter.dto.Metric;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

@Processor
@RequiredArgsConstructor
public class MetricProcessor<T extends Metric> {

    private final ByteArrayMetricDecompressor decompressor;

    private final ObjectMapper objectMapper;

    public MetadataWrapper<T> process(final byte[] data) {

        byte[] unzipped = decompressor.unzip(data);

        try {
            return objectMapper.readValue(unzipped, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
