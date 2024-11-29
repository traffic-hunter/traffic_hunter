package ygo.traffic_hunter.core.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.common.map.TransactionMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;
import ygo.traffic_hunter.core.websocket.handler.MetricWebSocketHandler;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;
import ygo.traffic_hunter.domain.entity.TransactionMeasurement;
import ygo.traffic_hunter.domain.interval.TimeInterval;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MetricService {

    private final TransactionMapper transactionMapper;

    private final SystemInfoMapper systemInfoMapper;

    private final MetricRepository metricRepository;

    private final ServerSentEventManager sseManager;

    private final MetricWebSocketHandler webSocketHandler;

    public MetricService(@Qualifier("serverSentEventViewManager") final ServerSentEventManager sseManager,
                         final MetricWebSocketHandler webSocketHandler,
                         final TransactionMapper transactionMapper,
                         final SystemInfoMapper systemInfoMapper,
                         final MetricRepository metricRepository) {

        this.transactionMapper = transactionMapper;
        this.webSocketHandler = webSocketHandler;
        this.systemInfoMapper = systemInfoMapper;
        this.metricRepository = metricRepository;
        this.sseManager = sseManager;
    }

    public List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                          final String agentName) {

        List<MetricMeasurement> metrics = metricRepository.findMetricsByRecentTimeAndAgentName(interval, agentName);

        return metrics.stream()
                .map(systemInfoMapper::map)
                .toList();
    }

    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName) {

        List<TransactionMeasurement> metrics = metricRepository.findTxMetricsByRecentTimeAndAgentName(interval,
                agentName);

        return metrics.stream()
                .map(transactionMapper::map)
                .toList();
    }

    public SseEmitter register(final SseEmitter sseEmitter) {
        return sseManager.register(sseEmitter);
    }

    public void broadcast(final TimeInterval interval) {

        for (AgentMetadata metadata : webSocketHandler.getAgents()) {

            List<SystemMetricResponse> metrics = findMetricsByRecentTimeAndAgentName(
                    interval,
                    metadata.agentName()
            );

            List<TransactionMetricResponse> txMetrics = findTxMetricsByRecentTimeAndAgentName(
                    interval,
                    metadata.agentName()
            );

            sseManager.asyncSendAll(metrics);
            sseManager.asyncSendAll(txMetrics);
        }
    }
}
