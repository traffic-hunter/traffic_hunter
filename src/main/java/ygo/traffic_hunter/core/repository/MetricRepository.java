package ygo.traffic_hunter.core.repository;

import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.TransactionMeasurement;

public interface MetricRepository {

    void save(MetricMeasurement metric);

    void save(TransactionMeasurement metric);
}
