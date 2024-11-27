package ygo.traffichunter.agent.engine.sender;

import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;

public interface MetricSender {

    void toSend(AgentMetadata metadata);
}
