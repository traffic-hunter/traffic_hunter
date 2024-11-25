package ygo.traffic_hunter.core.sse.alert;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;

@Slf4j
@Component
public class ServerSentEventAlertManager implements ServerSentEventManager {

    @Override
    public SseEmitter register(final SseEmitter sseEmitter) {
        return null;
    }

    @Override
    public <T> void send(final T data) {

    }

    @Override
    public <T> void sendAll(final List<T> data) {

    }

    @Override
    public int size() {
        return 0;
    }
}
