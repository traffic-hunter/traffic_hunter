package ygo.traffichunter.agent.event.consume;

public interface EventConsumer<T> {

    T consume();
}
