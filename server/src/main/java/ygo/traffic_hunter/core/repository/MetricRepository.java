/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 yungwang-o
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.core.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ygo.traffic_hunter.core.dto.response.RealTimeMonitoringResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.dto.response.metric.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricAvgResponse;
import ygo.traffic_hunter.core.dto.response.statistics.metric.StatisticsMetricMaxResponse;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;
import ygo.traffic_hunter.core.statistics.StatisticsMetricTimeRange;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 */
public interface MetricRepository extends AgentRepository {

    void save(MetricMeasurement metric);

    void save(TransactionMeasurement metric);

    List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(TimeInterval interval, String agentName,
                                                                   Integer limit);

    List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(TimeInterval interval, String agentName,
                                                                          Integer limit);

    Slice<ServiceTransactionResponse> findServiceTransaction(Pageable pageable);

    StatisticsMetricMaxResponse findMaxMetricByTimeInterval(StatisticsMetricTimeRange timeRange);

    StatisticsMetricAvgResponse findAvgMetricByTimeInterval(StatisticsMetricTimeRange timeRange);

    List<RealTimeMonitoringResponse> findRealtimeMonitoringByTimeInterval(TimeInterval timeInterval, String agentName,
                                                                          Integer limit);
}
