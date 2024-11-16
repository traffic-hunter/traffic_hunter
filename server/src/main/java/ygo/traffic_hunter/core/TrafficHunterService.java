package ygo.traffic_hunter.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ygo.traffic_hunter.common.map.DataToMeasurementMapper;
import ygo.traffic_hunter.common.map.TransactionMeasurementMapper;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.TransactionMeasurement;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.TransactionInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.metadata.MetadataWrapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficHunterService {

    private final MetricRepository metricRepository;

    private final DataToMeasurementMapper mapper;

    private final TransactionMeasurementMapper txMapper;

    public void save(final MetadataWrapper<SystemInfo> systemInfo) {

    }

    public void save(final TransactionInfo transactionInfo) {

        final TransactionMeasurement transactionMeasurement = txMapper.map(transactionInfo);

        metricRepository.save(transactionMeasurement);
    }
}
