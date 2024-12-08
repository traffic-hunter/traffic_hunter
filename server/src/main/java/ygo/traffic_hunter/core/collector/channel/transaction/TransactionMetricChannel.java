package ygo.traffic_hunter.core.collector.channel.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.collector.channel.MetricChannel;
import ygo.traffic_hunter.core.collector.processor.MetricProcessor;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.event.channel.TransactionMetricEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMetricChannel implements MetricChannel {

    private final MetricProcessor processor;

    private final ApplicationEventPublisher publisher;

    @Override
    public MetricHeaderSpec getHeaderSpec() {
        return MetricHeaderSpec.TRANSACTION;
    }

    @Override
    public void open(final byte[] payload) {

        MetadataWrapper<TransactionInfo> object = processor.processTransactionInfo(payload);

        log.info("Transaction metric data: {}", object);

        publisher.publishEvent(new TransactionMetricEvent(object));
    }
}
