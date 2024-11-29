package ygo.traffic_hunter.core.channel.collector.handler.transaction;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;

@Slf4j
public class TransactionMetricHandler implements MetricHandler {

    private final MetricProcessor processor;

    private final TransactionMapper mapper;

    @Builder
    public TransactionMetricHandler(final MetricProcessor processor,
                                    final TransactionMapper mapper) {
        this.processor = processor;
        this.mapper = mapper;
    }

    @Override
    public void handle(final byte[] payload, final MetricValidator validator, final MetricRepository repository) {
        MetadataWrapper<TransactionInfo> object = processor.processTransactionInfo(payload);

        if(validator.validate(object)) {
            TransactionMeasurement measurement = mapper.map(object);

            repository.save(measurement);
        }
    }
}
