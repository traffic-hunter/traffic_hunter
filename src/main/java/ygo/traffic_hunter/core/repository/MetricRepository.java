package ygo.traffic_hunter.core.repository;

import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;

public interface MetricRepository {

    void save(MetricMeasurement metric);
}
