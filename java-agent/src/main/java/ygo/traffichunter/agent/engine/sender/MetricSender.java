package ygo.traffichunter.agent.engine.sender;

public interface MetricSender<O> {

    O toSend();
}
