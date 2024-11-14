package ygo.traffichunter.agent.engine.sender;

import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;

public interface MetricSender {

    default void toSend(AgentMetadata metadata) {}
}
