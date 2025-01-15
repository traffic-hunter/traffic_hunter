package ygo.traffic_hunter.core.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.interval.TimeInterval;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ServerSentEventManagers {

    Map<String, ServerSentEventManager> eventManagers = new ConcurrentHashMap<>();

    public SseEmitter register(String id, SseEmitter emitter) {

        log.info("registering sse emitter {}", emitter);
        eventManagers.put(id, new ServerSentEventManager(id, emitter, Executors.newSingleThreadScheduledExecutor()));

        emitter.onCompletion(() -> {
            log.info("completed sse emitter {}", emitter);
            eventManagers.remove(id);
        });

        emitter.onTimeout(() -> {
            log.info("timed out sse emitter {}", emitter);
            emitter.complete();
        });
        return emitter;
    }

    public void scheduleBroadcast(String id, TimeInterval timeInterval, Runnable runnable) {
        ServerSentEventManager serverSentEventManager = eventManagers.get(id);
        serverSentEventManager.scheduleBroadcast(timeInterval, runnable);
    }

    public <T> void send(final String id, final T data) {
        ServerSentEventManager serverSentEventManager = eventManagers.get(id);
        serverSentEventManager.send(data);
    }
}
