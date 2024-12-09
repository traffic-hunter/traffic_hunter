package ygo.traffic_hunter.core.sse.view;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import ygo.traffic_hunter.core.sse.ServerSentEventManager;

/**
 * <p>
 * The {@code ServerSentEventViewManager} class manages Server-Sent Events (SSE)
 * connections for updating client-side rendered (CSR) views in frameworks like
 * React.js or Vue.js.
 * </p>
 *
 * <h4>Core Responsibilities</h4>
 * <ul>
 *     <li>Registers and manages {@link SseEmitter} instances.</li>
 *     <li>Sends data synchronously to all connected clients.</li>
 *     <li>Handles emitter lifecycle events, such as completion and timeout.</li>
 * </ul>
 *
 * <h4>Workflow</h4>
 * <ol>
 *     <li>Register a new {@link SseEmitter} for an SSE connection.</li>
 *     <li>Send data to all registered clients synchronously.</li>
 *     <li>Manage emitter lifecycle events:
 *         <ul>
 *             <li>Remove the emitter when the connection is completed.</li>
 *             <li>Handle timeouts by completing the emitter.</li>
 *         </ul>
 *     </li>
 * </ol>
 *
 * <h4>Thread Safety</h4>
 * <p>
 * Uses {@link CopyOnWriteArrayList} to safely manage concurrent access to the list
 * of {@link SseEmitter} instances.
 * </p>
 *
 * @see SseEmitter
 *
 * @author yungwang-o
 * @version 1.0.0
 */
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
    public <T> void send(final List<T> data) {

        for(SseEmitter emitter : emitters) {
            sendAll(data, emitter);
        }
    }

    @Override
    public <T> void asyncSend(final T data) {

    }

    @Override
    public <T> void asyncSend(final List<T> data) {

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
