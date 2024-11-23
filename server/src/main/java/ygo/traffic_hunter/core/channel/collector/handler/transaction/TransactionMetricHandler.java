package ygo.traffic_hunter.core.channel.collector.handler.transaction;

import lombok.Builder;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;

public class TransactionMetricHandler implements MetricHandler {

    private final MetricProcessor<TransactionInfo> processor;

    private final TransactionMapper mapper;

    @Builder
    public TransactionMetricHandler(final MetricProcessor<TransactionInfo> preprocessor,
                                    final TransactionMapper mapper) {
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
