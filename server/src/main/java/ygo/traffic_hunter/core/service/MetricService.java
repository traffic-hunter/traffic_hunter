/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;
import ygo.traffic_hunter.core.sse.ServerSentEventManagers;
import ygo.traffic_hunter.core.websocket.handler.MetricWebSocketHandler;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class MetricService {

    private final MetricRepository metricRepository;

    private final ServerSentEventManagers sseManagers;

    private final MetricWebSocketHandler webSocketHandler;

    private final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

    public MetricService(ServerSentEventManagers sseManagers,
                         final MetricWebSocketHandler webSocketHandler,
                         final MetricRepository metricRepository) {

        this.webSocketHandler = webSocketHandler;
        this.metricRepository = metricRepository;
        this.sseManagers = sseManagers;
    }

    public List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                          final String agentName) {

        return metricRepository.findMetricsByRecentTimeAndAgentName(interval, agentName);
    }

    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName) {

        return metricRepository.findTxMetricsByRecentTimeAndAgentName(interval, agentName);
    }

    public SseEmitter register(final String id, final SseEmitter sseEmitter) {
        return sseManagers.register(id, sseEmitter);
    }

    public void asyncBroadcast(final TimeInterval interval) {

    }

    public void scheduleBroadcast(final String id, @NonNull final TimeInterval interval) {
        sseManagers.scheduleBroadcast(id, interval, () -> broadcast(id, interval));
    }

    private void broadcast(final String id, final TimeInterval interval) {
        List<AgentMetadata> agents = webSocketHandler.getAgents();

        for (AgentMetadata metadata : agents) {
            processMetrics(id, interval, metadata);
        }
    }

    private void processMetrics(String id, TimeInterval interval, AgentMetadata metadata) {
        List<SystemMetricResponse> metrics = findMetricsByRecentTimeAndAgentName(interval, metadata.agentName());
        List<TransactionMetricResponse> txMetrics = findTxMetricsByRecentTimeAndAgentName(interval, metadata.agentName());

        sseManagers.send(id, metrics);
        sseManagers.send(id, txMetrics);
    }
}
