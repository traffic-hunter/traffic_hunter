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

/**
 * The {@code AgentTransactionMetricSender} class is responsible for sending transaction metrics
 * to the server via a WebSocket connection.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Continuously retrieves transaction data from a synchronized queue.</li>
 *     <li>Wraps the transaction data with metadata and sends it in a compressed format.</li>
 *     <li>Relies on {@link SyncQueue} for thread-safe access to transaction data.</li>
 * </ul>
 *
 * <p>Thread Safety:</p>
 * <ul>
 *     <li>This class relies on {@link SyncQueue}, which provides thread-safe operations for data retrieval.</li>
 *     <li>The {@code toSend} method runs in a blocking loop, making it suitable for dedicated threads.</li>
 * </ul>
 *
 * @see MetricSender
 * @see MetricWebSocketClient
 * @see SyncQueue
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
