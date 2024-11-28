package ygo.traffichunter.agent.engine.sender.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.websocket.AgentSystemMetricSender;
import ygo.traffichunter.agent.engine.sender.websocket.AgentTransactionMetricSender;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.util.AgentUtil;
import ygo.traffichunter.websocket.MetricWebSocketClient;

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

        this.client = new MetricWebSocketClient(AgentUtil.WEBSOCKET_URL.getUri(property.serverUri()));
        this.client.connect();
        this.metadata = metadata;
        this.context = context;
        this.property = property;
        this.transactionMetricSender = new AgentTransactionMetricSender(client);
        this.systemMetricSender = new AgentSystemMetricSender(client, property);
        this.schedule = Executors.newSingleThreadScheduledExecutor(getThreadFactory("TransactionSystemInfoMetricSender"));
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void afterConnectionEstablished() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        try {
            client.send(mapper.writeValueAsString(metadata));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

        log.info("start Metric send...");

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

        executor.execute(Retry.decorateRunnable(retry, () ->
                transactionMetricSender.toSend(metadata)
        ));

        schedule.scheduleWithFixedDelay(Retry.decorateRunnable(retry, () ->
                        systemMetricSender.toSend(metadata)
                ),
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
