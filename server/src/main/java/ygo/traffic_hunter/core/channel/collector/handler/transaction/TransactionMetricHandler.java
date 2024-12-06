package ygo.traffic_hunter.core.channel.collector.handler.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.channel.collector.handler.MetricHandler;
import ygo.traffic_hunter.core.channel.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.channel.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMetricHandler implements MetricHandler {

    private final MetricProcessor processor;

    private final TransactionMapper mapper;

    private final MetricValidator validator;

    private final MetricRepository repository;

    @Override
    public MetricHeaderSpec getHeaderSpec() {
        return MetricHeaderSpec.TRANSACTION;
    }

    @Override
    @Transactional
    public void handle(final byte[] payload) {
        MetadataWrapper<TransactionInfo> object = processor.processTransactionInfo(payload);

        if(validator.validate(object)) {
            TransactionMeasurement measurement = mapper.map(object);

            repository.save(measurement);
        }
    }
}
