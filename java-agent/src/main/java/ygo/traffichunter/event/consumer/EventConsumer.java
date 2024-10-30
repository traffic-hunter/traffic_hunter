package ygo.traffichunter.event.consumer;

import java.util.List;

public interface EventConsumer<T> {

    T consume();

    List<T> pressure();
}
