package ygo.traffic_hunter.core.repository;

import java.util.List;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;

public interface MetricRepository extends AgentRepository {

    void save(MetricMeasurement metric);

    void save(TransactionMeasurement metric);

    List<MetricMeasurement> findMetricsByRecentTimeAndAgentName(final TimeInterval interval, final String agentName);

    List<TransactionMeasurement> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval, final String agentName);
}
