package ygo.traffic_hunter.core.event.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.collector.validator.MetricValidator;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;

@Component
@RequiredArgsConstructor
public class ChannelEventHandler {

    private final SystemInfoMapper systemInfoMapper;

    private final TransactionMapper transactionMapper;

    private final MetricValidator validator;

    private final MetricRepository repository;

    @EventListener
    @Transactional
    public void handle(final TransactionMetricEvent event) {

        MetadataWrapper<TransactionInfo> object = event.transactionInfo();

        if(validator.validate(object)) {
            return;
        }

        TransactionMeasurement measurement = transactionMapper.map(object);

        repository.save(measurement);
    }

    @EventListener
    @Transactional
    public void handle(final SystemInfoMetricEvent event) {

        MetadataWrapper<SystemInfo> object = event.systemInfo();

        if(validator.validate(object)) {
            return;
        }

        MetricMeasurement measurement = systemInfoMapper.map(object);

        repository.save(measurement);
    }
}
