package ygo.traffichunter.agent.engine.sender.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.metric.metadata.MetadataWrapper;
import ygo.traffichunter.agent.engine.metric.transaction.TraceInfo;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.websocket.MetricWebSocketClient;
import ygo.traffichunter.websocket.converter.SerializationByteArrayConverter.MetricType;

/**
 * <p>
 * The {@code AgentTransactionMetricSender} class is responsible for sending transaction metrics
 * to the server via a WebSocket connection.
 * </p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Continuously retrieves transaction data from a synchronized queue.</li>
 *     <li>Wraps the transaction data with metadata and sends it in a compressed format.</li>
 *     <li>Relies on {@link SyncQueue} for thread-safe access to transaction data.</li>
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

    public AgentTransactionMetricSender(final MetricWebSocketClient client) {
        this.client = client;
    }

    @Override
    public void toSend(final AgentMetadata metadata)  {

        while (!Thread.currentThread().isInterrupted()) {

            try {

                TraceInfo trInfo = SyncQueue.INSTANCE.poll();

                MetadataWrapper<TraceInfo> wrapper = MetadataWrapper.create(metadata, trInfo);

                client.compressToSend(wrapper, MetricType.TRANSACTION_METRIC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IllegalStateException e) {
                log.error("exception while sending transaction metric = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
