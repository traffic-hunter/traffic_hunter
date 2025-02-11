package ygo.traffic_hunter.core.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.identification.Identification;
import ygo.traffic_hunter.core.schedule.Scheduler;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author JuSeong
 * @version 1.1.0
 */

@Slf4j
@Component
public class ServerSentEventManager {

    private final Map<Identification, Client> clients = new ConcurrentHashMap<>();

    public SseEmitter register(final Identification identification, final SseEmitter emitter) {

        log.info("registering sse emitter {}", emitter);

        Client client = new Client(identification, emitter,
                new Scheduler(Executors.newSingleThreadScheduledExecutor()));

        clients.put(identification, client);

        client.send("connect");

        emitter.onCompletion(() -> {
            log.info("completed sse emitter {}", emitter);
            clients.remove(identification);
        });

        emitter.onTimeout(() -> {
            log.info("timed out sse emitter {}", emitter);
            emitter.complete();
        });

        return emitter;
    }

    public void scheduleBroadcast(final Identification identification,
                                  final TimeInterval timeInterval,
                                  final Runnable runnable) {

        if (!clients.containsKey(identification)) {
            throw new IllegalStateException(
                    "The client with the given identification does not exist. Please subscribe first.");
        }

        log.info("schedule broadcasting sse emitter {}", timeInterval);

        Client client = clients.get(identification);
        client.scheduleBroadcast(timeInterval, runnable);
    }

    public <T> void send(final Identification identification, final T data) {

        Client client = clients.get(identification);
        client.send(data);
    }
}
