package ygo.traffic_hunter.core.sse;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <p>
 * The {@code ServerSentEventManager} interface provides a contract for managing
 * Server-Sent Events (SSE) and sending data to clients. This interface is typically
 * implemented by classes designed for specific purposes, such as managing UI updates
 * for client-side rendering (CSR) frameworks like React.js or Vue.js, or handling
 * alert notifications.
 * </p>
 *
 * @see SseEmitter
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface ServerSentEventManager {

    SseEmitter register(SseEmitter sseEmitter);

    <T> void send(T data);

    <T> void send(List<T> data);

    <T> void asyncSend(T data);

    <T> void asyncSend(List<T> data);

    int size();
}
