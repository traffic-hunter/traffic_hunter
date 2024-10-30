package ygo.traffichunter.event.publisher;

public interface EventPublisher<T> {

    void publish(T event);
}
