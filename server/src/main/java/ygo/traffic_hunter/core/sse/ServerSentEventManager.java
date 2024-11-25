package ygo.traffic_hunter.core.sse;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ServerSentEventManager {

    SseEmitter register(SseEmitter sseEmitter);

    <T> void send(T data);

    <T> void sendAll(List<T> data);

    int size();
}
