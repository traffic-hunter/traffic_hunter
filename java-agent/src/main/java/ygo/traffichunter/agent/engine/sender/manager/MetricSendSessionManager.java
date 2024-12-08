package ygo.traffichunter.agent.engine.sender.manager;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.websocket.AgentSystemMetricSender;
import ygo.traffichunter.agent.engine.sender.websocket.AgentTransactionMetricSender;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.util.AgentUtil;
import ygo.traffichunter.websocket.MetricWebSocketClient;

/**
 * The {@code MetricSendSessionManager} class manages the session for sending
 * transaction and system metrics from the TrafficHunter Agent to a server.
 * It handles WebSocket connections, retries, scheduling, and lifecycle management
 * of the metric-sending operations.
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Manages WebSocket connections for real-time metric transmission.</li>
 *     <li>Schedules periodic system metric transmissions using a {@link ScheduledExecutorService}.</li>
 *     <li>Retries connection attempts in case of failures with a configurable back-off policy.</li>
 *     <li>Provides lifecycle management for starting and stopping the session.</li>
 * </ul>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>{@code run()} - Starts the metric sending session, ensuring retries and scheduling are properly configured.</li>
 *     <li>{@code close()} - Safely shuts down the session, releasing all resources.</li>
 *     <li>Integrates with {@link Retry} to handle WebSocket reconnections in case of failures.</li>
 * </ul>
 *
 * <p>Thread Management:</p>
 * <ul>
 *     <li>Uses a {@link ScheduledExecutorService} for scheduling periodic system metrics.</li>
 *     <li>Uses a {@link ExecutorService} for running transaction metric transmissions.</li>
 *     <li>Both executors are safely shut down during the {@code close()} method.</li>
 * </ul>
 *
 * <p>Limitations:</p>
 * <ul>
 *     <li>If the agent's context status is already {@code RUNNING}, the session cannot be restarted without stopping it first.</li>
 *     <li>Retries only handle specific exceptions, as configured in the {@link RetryHelper}.</li>
 * </ul>
 *
 * @see Retry
 * @see ScheduledExecutorService
 * @see MetricWebSocketClient
 * @see TrafficHunterAgentProperty
 * @see AgentExecutableContext
 * @see AgentMetadata
 * @see RetryHelper
 * @author yungwang-o
 * @version 1.0.0
 */
public class MetricSendSessionManager {

    private static final Logger log = LoggerFactory.getLogger(MetricSendSessionManager.class);

    private final TrafficHunterAgentProperty property;

    private final AgentTransactionMetricSender transactionMetricSender;

    private final AgentSystemMetricSender systemMetricSender;

    private final ScheduledExecutorService schedule;

    private final ExecutorService executor;

    private final AgentExecutableContext context;

    private final AgentMetadata metadata;

    private final MetricWebSocketClient client;

    public MetricSendSessionManager(final TrafficHunterAgentProperty property,
                                    final AgentExecutableContext context,
                                    final AgentMetadata metadata) {

        this.client = new MetricWebSocketClient(AgentUtil.WEBSOCKET_URL.getUri(property.serverUri()), metadata);
        this.client.connect();
        this.metadata = metadata;
        this.context = context;
        this.property = property;
        this.transactionMetricSender = new AgentTransactionMetricSender(client);
        this.systemMetricSender = new AgentSystemMetricSender(client, property);
        this.schedule = Executors.newSingleThreadScheduledExecutor(getThreadFactory("TransactionSystemInfoMetricSender"));
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void run() {

        if(context.isStopped()) {
            log.error("MetricSendSessionManager is stopped");
            return;
        }

        if(context.isRunning()) {
            log.error("MetricSendSessionManager is already running");
            return;
        }

        log.info("start Metric send!!");

        final RetryHelper retryHelper = RetryHelper.builder()
                .backOffPolicy(property.backOffPolicy())
                .isCheck(true)
                .retryName("websocket retry")
                .maxAttempts(property.maxAttempt())
                .retryPredicate(throwable -> throwable instanceof IllegalStateException)
                .build();

        RetryConfig retryConfig = retryHelper.configureRetry();

        Retry retry = Retry.of(retryHelper.getRetryName(), retryConfig);

        retry.getEventPublisher()
                        .onRetry(event -> {
                            client.reconnect();
                            log.info("{} retry {} attempts...", event.getName(), event.getNumberOfRetryAttempts());
                        });

        context.setStatus(AgentStatus.RUNNING);

        executor.execute(Retry.decorateRunnable(retry, () -> transactionMetricSender.toSend(metadata)));

        schedule.scheduleWithFixedDelay(Retry.decorateRunnable(retry, () -> systemMetricSender.toSend(metadata)),
                0,
                property.scheduleInterval(),
                property.timeUnit()
        );
    }

    public void close() {
        log.info("closing MetricSendSessionManager...");
        context.setStatus(AgentStatus.EXIT);
        client.close();
        executor.shutdown();
        schedule.shutdown();
    }

    private ThreadFactory getThreadFactory(final String threadName) {
        return r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName(threadName);

            return thread;
        };
    }
}
