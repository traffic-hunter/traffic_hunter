package ygo.traffic_hunter.core.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.domain.interval.TimeInterval;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ServerSentEventManager {

    Map<String, Client> clients = new ConcurrentHashMap<>();

    public SseEmitter register(String id, SseEmitter emitter) {

        log.info("registering sse emitter {}", emitter);
        Client client = new Client(id, emitter, Executors.newSingleThreadScheduledExecutor());
        clients.put(id, client);
        client.send("connect");
        emitter.onCompletion(() -> {
            log.info("completed sse emitter {}", emitter);
            clients.remove(id);
        });

        emitter.onTimeout(() -> {
            log.info("timed out sse emitter {}", emitter);
            emitter.complete();
        });
        return emitter;
    }

    public void scheduleBroadcast(String id, TimeInterval timeInterval, Runnable runnable) {
        Client client = clients.get(id);
        client.scheduleBroadcast(timeInterval, runnable);
    }

    public <T> void send(final String id, final T data) {
        Client client = clients.get(id);
        client.send(data);
    }
}
