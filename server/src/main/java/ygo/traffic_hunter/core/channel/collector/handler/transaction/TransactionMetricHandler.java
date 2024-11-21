package ygo.traffic_hunter.core.channel.collector.handler.transaction;

import lombok.Builder;
import ygo.traffic_hunter.common.map.TransactionMeasurementMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.dto.systeminfo.TransactionInfo;
import ygo.traffic_hunter.dto.systeminfo.metadata.MetadataWrapper;

public class TransactionMetricHandler implements MetricHandler {

    private final MetricProcessor<TransactionInfo> processor;

    private final TransactionMeasurementMapper mapper;

    @Builder
    public TransactionMetricHandler(final MetricProcessor<TransactionInfo> preprocessor,
                                    final TransactionMeasurementMapper mapper) {
        this.processor = preprocessor;
        this.mapper = mapper;
    }

    @Override
    public void handle(final byte[] payload, final MetricValidator validator, final MetricRepository repository) {
        MetadataWrapper<TransactionInfo> object = processor.process(payload);

        if(validator.validate(object)) {
            repository.save(mapper.map(object));
        }
    }
}
