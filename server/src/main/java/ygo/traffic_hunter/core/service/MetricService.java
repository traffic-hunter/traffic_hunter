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
import ygo.traffic_hunter.core.websocket.handler.MetricWebSocketHandler;
import ygo.traffic_hunter.domain.interval.TimeInterval;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MetricService {

    private final MetricRepository metricRepository;

    private final ServerSentEventManager sseManager;

    private final MetricWebSocketHandler webSocketHandler;

    private final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

    public MetricService(@Qualifier("serverSentEventViewManager") final ServerSentEventManager sseManager,
                         final MetricWebSocketHandler webSocketHandler,
                         final MetricRepository metricRepository) {

        this.webSocketHandler = webSocketHandler;
        this.metricRepository = metricRepository;
        this.sseManager = sseManager;
    }

    public List<SystemMetricResponse> findMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                          final String agentName) {

        return metricRepository.findMetricsByRecentTimeAndAgentName(interval, agentName);
    }

    public List<TransactionMetricResponse> findTxMetricsByRecentTimeAndAgentName(final TimeInterval interval,
                                                                                 final String agentName) {

        return metricRepository.findTxMetricsByRecentTimeAndAgentName(interval, agentName);
    }

    public SseEmitter register(final SseEmitter sseEmitter) {
        return sseManager.register(sseEmitter);
    }

    public void asyncBroadcast(final TimeInterval interval) {

    }

    public void scheduleBroadcast(@NonNull final TimeInterval interval) {
        schedule.scheduleWithFixedDelay(() -> broadcast(interval),
                0,
                interval.getDelayMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    private void broadcast(final TimeInterval interval) {

        for (AgentMetadata metadata : webSocketHandler.getAgents()) {

            List<SystemMetricResponse> metrics = findMetricsByRecentTimeAndAgentName(
                    interval,
                    metadata.agentName()
            );

            List<TransactionMetricResponse> txMetrics = findTxMetricsByRecentTimeAndAgentName(
                    interval,
                    metadata.agentName()
            );

            sseManager.send(metrics);
            sseManager.send(txMetrics);
        }
    }
}
