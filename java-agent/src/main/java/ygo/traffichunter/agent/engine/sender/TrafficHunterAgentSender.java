package ygo.traffichunter.agent.engine.sender;

public interface TrafficHunterAgentSender<I, O> {

    O toSend(I input) throws InterruptedException;
}
