package ygo.traffic_hunter.core.send;

public interface AsyncSender {

    <T> void asyncSend(T data);
}
