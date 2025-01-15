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
package ygo.traffic_hunter.core.sse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.domain.interval.TimeInterval;

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
@Slf4j
public class ServerSentEventManager {

    private final String id;
    private final SseEmitter emitter;
    private final ScheduledExecutorService schedule;

    public ServerSentEventManager(String id, SseEmitter emitter, ScheduledExecutorService schedule) {
        this.id = id;
        this.emitter = emitter;
        this.schedule = schedule;
    }

    public <T> void scheduleBroadcast(TimeInterval interval, Runnable runnable) {
        schedule.shutdownNow();
        schedule.scheduleWithFixedDelay(runnable ,
                0,
                interval.getDelayMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    public <T> void send(final T data) {
        send(data, emitter);
    }

    public <T> void send(final List<T> data) {
        sendAll(data, emitter);
    }

    private <T> void sendAll(final List<T> data, final SseEmitter emitter) {
        SseEmitter.SseEventBuilder sseBuilder = SseEmitter.event()
                .name(emitter.toString())
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void send(final T data, final SseEmitter emitter) {
        SseEmitter.SseEventBuilder sseBuilder = SseEmitter.event()
                .name(emitter.toString())
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
