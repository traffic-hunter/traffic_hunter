package ygo.traffichunter.agent.engine.sender.websocket;

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
import ygo.traffichunter.util.AgentUtil;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements MetricSender {

    public static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final TrafficHunterAgentProperty property;

    private final MetricWebSocketClient<MetadataWrapper<TransactionInfo>> client;

    public static SyncQueue syncQueue = SyncQueue.INSTANCE;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property) {

        this.property = property;
        this.client = new MetricWebSocketClient<>(URI.create(AgentUtil.WEBSOCKET_URL.getUrl(property.uri())));
        this.client.connect();
    }

    @Override
    public void toSend(final AgentMetadata metadata)  {

        while (true) {
            try {
                TransactionInfo txInfo = syncQueue.poll();

                RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                        .failAfterMaxAttempts(true)
                        .retryName("websocket")
                        .throwable(throwable -> throwable instanceof IllegalStateException)
                        .retrySupplier(() -> this.sendSupport(txInfo, metadata));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    public MetadataWrapper<TransactionInfo> sendSupport(final TransactionInfo input, final AgentMetadata metadata) {
        MetadataWrapper<TransactionInfo> wrapper = MetadataWrapper.create(metadata, input);
        client.toSend(wrapper);
        return wrapper;
    }

    public void close() {
        client.close();
    }
}
