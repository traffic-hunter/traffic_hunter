package ygo.traffichunter.agent.engine.sender;

import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;

public interface MetricSender<O> {

    default O toSend() { return null; };

    default O toSend(TransactionInfo input) { return null; }
}
