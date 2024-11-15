package ygo.traffichunter.agent.engine.sender.websocket;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.systeminfo.metadata.MetadataWrapper;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.retry.backoff.BackOffPolicy;
import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.util.AgentUtil;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements MetricSender {

    public static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final MetricWebSocketClient<MetadataWrapper<TransactionInfo>> client;

    public static SyncQueue syncQueue = SyncQueue.INSTANCE;

    public final RetryHelper retryHelper;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property) {

        this.client = new MetricWebSocketClient<>(URI.create(AgentUtil.WEBSOCKET_URL.getUrl(property.uri())));
        this.client.connect();
        this.retryHelper = RetryHelper.builder()
                .backOffPolicy(property.backOffPolicy())
                .isCheck(true)
                .retryName("websocket retry")
                .maxAttempts(property.maxAttempt())
                .retryPredicate(throwable -> throwable instanceof RuntimeException)
                .build();
    }

    @Override
    public void toSend(final AgentMetadata metadata)  {

        RetryConfig retryConfig = retryHelper.configureRetry();

        Retry retry = Retry.of(retryHelper.getRetryName(), retryConfig);

        retry.getEventPublisher()
                .onRetry(event -> {
                    client.reconnect();
                    log.info("{} retry {} attempts...", event.getName(), event.getNumberOfRetryAttempts());
                });

        while (true) {
            try {
                TransactionInfo txInfo = syncQueue.poll();

                Retry.decorateSupplier(retry, () -> this.send(txInfo, metadata)).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private MetadataWrapper<TransactionInfo> send(final TransactionInfo input, final AgentMetadata metadata) {
        MetadataWrapper<TransactionInfo> wrapper = MetadataWrapper.create(metadata, input);
        client.toSend(wrapper);
        return wrapper;
    }

    public void close() {
        client.close();
    }
}
