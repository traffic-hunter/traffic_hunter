package ygo.traffic_hunter.core.send;

import java.util.List;

public interface Sender {

    <T> void send(T data);

    <T> void send(List<T> data);
}
