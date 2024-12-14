/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
