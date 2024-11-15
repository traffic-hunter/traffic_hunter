package ygo.traffichunter.agent.engine.sender;

import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;

public interface MetricSender {

    void toSend(AgentMetadata metadata);
}
