package ygo.traffic_hunter.core.repository;

import java.util.List;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public interface MetricRepository extends AgentRepository {

    void save(MetricMeasurement metric);

    void save(TransactionMeasurement metric);

    List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval, final String agentName);

    List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval, final String agentName);
}
