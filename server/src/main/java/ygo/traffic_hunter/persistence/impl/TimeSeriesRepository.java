package ygo.traffic_hunter.persistence.impl;

import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.dto.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.TransactionMeasurement;

@Repository
@RequiredArgsConstructor
public class TimeSeriesRepository implements MetricRepository {

    private final WriteApiBlocking writeApi;

    @Override
    public void save(final MetricMeasurement metric) {
        writeApi.writeMeasurement(WritePrecision.MS, metric);
    }

    @Override
    public void save(final TransactionMeasurement metric) {
        writeApi.writeMeasurement(WritePrecision.MS, metric);
    }
}
