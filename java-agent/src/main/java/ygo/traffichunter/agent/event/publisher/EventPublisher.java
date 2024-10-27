package ygo.traffichunter.agent.event.publisher;

public interface EventPublisher<T> {

    void publish(T obj);
}
