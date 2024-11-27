package ygo.traffichunter.agent.engine.sender.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.metric.transaction.TransactionInfo;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.metric.metadata.MetadataWrapper;
import ygo.traffichunter.websocket.MetricWebSocketClient;
import ygo.traffichunter.websocket.converter.SerializationByteArrayConverter.MetricType;

public class AgentTransactionMetricSender implements MetricSender {

    public static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final MetricWebSocketClient client;

    public static SyncQueue syncQueue = SyncQueue.INSTANCE;

    public AgentTransactionMetricSender(final MetricWebSocketClient client) {
        this.client = client;
    }

    @Override
    public void toSend(final AgentMetadata metadata)  {

        while (true) {
            try {
                TransactionInfo txInfo = syncQueue.poll();

                MetadataWrapper<TransactionInfo> wrapper = MetadataWrapper.create(metadata, txInfo);

                client.compressToSend(wrapper, MetricType.TRANSACTION_METRIC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
