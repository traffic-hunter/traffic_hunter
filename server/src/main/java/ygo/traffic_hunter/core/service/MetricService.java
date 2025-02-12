/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.core.service;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.response.RealTimeMonitoringResponse;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.identification.Identification;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;
import ygo.traffic_hunter.core.websocket.handler.MetricWebSocketHandler;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MetricService {

    private static final TimeInterval DEFAULT_BROADCAST_INTERVAL = TimeInterval.REAL_TIME;

    private final MetricRepository metricRepository;

    private final ServerSentEventManager sseManager;

    private final MetricWebSocketHandler webSocketHandler;

    public List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                          final String agentName,
                                                                          final Integer limit) {

        return metricRepository.findMetricsByRecentTimeAndAgentName(interval, agentName, limit);
    }

    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName,
                                                                                 final Integer limit) {

        return metricRepository.findTxMetricsByRecentTimeAndAgentName(interval, agentName, limit);
    }

    public SseEmitter register(final Identification identification, final SseEmitter sseEmitter) {

        return sseManager.register(identification, sseEmitter);
    }

    public void asyncBroadcast(final TimeInterval interval) {

    }

    public void scheduleBroadcast(final Identification identification,
                                  @NonNull final TimeInterval interval,
                                  final Integer limit) {

        sseManager.scheduleBroadcast(identification, DEFAULT_BROADCAST_INTERVAL,
                () -> broadcast(identification, interval, limit));
    }

    private void broadcast(final Identification identification, final TimeInterval interval, final Integer limit) {

        try {
            List<AgentMetadata> agents = webSocketHandler.getAgents();

            for (AgentMetadata metadata : agents) {
                processMetrics(identification, interval, metadata, limit);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processMetrics(final Identification identification,
                                final TimeInterval interval,
                                final AgentMetadata metadata,
                                final Integer limit) {

        List<SystemMetricResponse> metrics = findMetricsByRecentTimeAndAgentName(
                interval,
                metadata.agentName(),
                limit
        );

        sseManager.send(identification, RealTimeMonitoringResponse.create(metrics));
    }
}
