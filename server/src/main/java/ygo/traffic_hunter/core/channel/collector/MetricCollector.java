package ygo.traffic_hunter.core.channel.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.map.DataToMeasurementMapperImpl;
import ygo.traffic_hunter.common.map.TransactionMeasurementMapperImpl;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.handler.systeminfo.SysteminfoMetricHandler;
import ygo.traffic_hunter.core.channel.collector.handler.transaction.TransactionMetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.processor.compress.ByteArrayMetricDecompressor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.repository.MetricRepository;

public class MetricCollector {

    private final Map<Byte, MetricHandler> map = new HashMap<>();

    private final MetricValidator validator;

    private final MetricRepository repository;

    private final ByteArrayMetricDecompressor decompressor;

    private final ObjectMapper mapper;

    @Builder
    public MetricCollector(final MetricValidator validator,
                           final MetricRepository repository,
                           final ByteArrayMetricDecompressor decompressor,
                           final ObjectMapper mapper) {

        this.validator = validator;
        this.repository = repository;
        this.decompressor = decompressor;
        this.mapper = mapper;
    }

    public void registerProcessors() {

        SysteminfoMetricHandler systeminfoMetricHandler = SysteminfoMetricHandler.builder()
                .mapper(new DataToMeasurementMapperImpl())
                .processor(new MetricProcessor<>(decompressor, mapper))
                .build();

        TransactionMetricHandler transactionMetricHandler = TransactionMetricHandler.builder()
                .mapper(new TransactionMeasurementMapperImpl())
                .preprocessor(new MetricProcessor<>(decompressor, mapper))
                .build();

        registerProcessor((byte) 1, systeminfoMetricHandler);
        registerProcessor((byte) 2, transactionMetricHandler);
    }

    public void collect(final ByteBuffer byteBuffer) {
        if(map.isEmpty()) {
            throw new IllegalStateException("collector is empty..");
        }

        byte[] data = convert(byteBuffer);

        byte header = data[0];

        MetricHandler metricHandler = Optional.of(map.get(header))
                .orElseThrow(() -> new IllegalArgumentException("Unknown header.."));

        metricHandler.handle(data, validator, repository);
    }

    public void clear() {
        map.clear();
    }

    private void registerProcessor(final byte header, final MetricHandler metricHandler) {
        map.put(header, metricHandler);
    }

    private byte[] convert(final ByteBuffer byteBuffer) {
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        return data;
    }
}
