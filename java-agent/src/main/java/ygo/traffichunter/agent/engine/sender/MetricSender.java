package ygo.traffichunter.agent.engine.sender;

import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.sender.websocket.AgentSystemMetricSender;
import ygo.traffichunter.agent.engine.sender.websocket.AgentTransactionMetricSender;

/**
 * The {@code MetricSender} interface defines the contract for sending metrics.
 * Implementations of this interface are responsible for serializing and sending
 * specific types of metrics using a given communication client.
 *
 * @see AgentTransactionMetricSender
 * @see AgentSystemMetricSender
 */
public interface MetricSender {

    void toSend(AgentMetadata metadata);
}
