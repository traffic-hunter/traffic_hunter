package ygo.traffic_hunter.core.sse.view;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;

@Slf4j
@Component
public class ServerSentEventViewManager implements ServerSentEventManager {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public SseEmitter register(final SseEmitter emitter) {
        log.info("registering sse emitter {}", emitter);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("completed sse emitter {}", emitter);
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.info("timed out sse emitter {}", emitter);
            emitter.complete();
        });

        return emitter;
    }

    @Override
    public <T> void send(final T data) {

        for(SseEmitter emitter : emitters) {
            send(data, emitter);
        }
    }

    @Override
    public <T> void sendAll(final List<T> data) {

        for(SseEmitter emitter : emitters) {
            sendAll(data, emitter);
        }
    }

    @Override
    public <T> void asyncSend(final T data) {

    }

    @Override
    public <T> void asyncSendAll(final List<T> data) {

    }

    @Override
    public int size() {
        return emitters.size();
    }

    private <T> void sendAll(final List<T> data, final SseEmitter emitter) {
        SseEventBuilder sseBuilder = SseEmitter.event()
                .name(emitter.toString())
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void send(final T data, final SseEmitter emitter) {
        SseEventBuilder sseBuilder = SseEmitter.event()
                .name(emitter.toString())
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
