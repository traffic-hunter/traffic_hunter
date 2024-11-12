package ygo.traffichunter.agent.engine.sender.websocket;

import java.net.URI;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.systeminfo.metadata.MetadataWrapper;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.util.AgentUtil;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements MetricSender<Supplier<MetadataWrapper<TransactionInfo>>>, Runnable {

    public static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final TrafficHunterAgentProperty property;

    private final MetricWebSocketClient<MetadataWrapper<TransactionInfo>> client;

    private final AgentMetadata metadata;

    public static SyncQueue sharedQueue = SyncQueue.INSTANCE;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property,
                                        final AgentMetadata metadata) {

        this.metadata = metadata;
        this.property = property;
        this.client = new MetricWebSocketClient<>(URI.create(AgentUtil.WEBSOCKET_URL.getUrl(property.uri())));
        this.client.connect();
    }

    @Override
    public void run() {
        while (true) {
            try {
                log.info("Queue hashcode in run: {}", System.identityHashCode(sharedQueue));
                log.info("Waiting for transaction data... size = {}", sharedQueue.size());
                log.info("transaction sender thread = {}, {}", Thread.currentThread().getName(), Thread.currentThread().getState());
                TransactionInfo info = sharedQueue.poll();
                log.info("Got transaction data: {}", info);
                send(info);
                log.info("Sent transaction data");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void send(final TransactionInfo txInfo)  {

        RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("websocket")
                .throwable(throwable -> throwable instanceof IllegalStateException)
                .retrySupplier(this.toSend(txInfo));
    }

    @Override
    public Supplier<MetadataWrapper<TransactionInfo>> toSend(final TransactionInfo input) {
        return () -> {

            MetadataWrapper<TransactionInfo> wrapper = new MetadataWrapper<>(metadata, input);
            client.toSend(wrapper);
            return wrapper;
        };
    }

    public void close() {
        client.close();
    }
}
