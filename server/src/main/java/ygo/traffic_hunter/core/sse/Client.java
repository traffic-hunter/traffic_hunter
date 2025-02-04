/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 yungwang-o
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.core.sse;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.identification.Identification;
import ygo.traffic_hunter.core.schedule.Scheduler;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 * @see SseEmitter
 */
@Slf4j
@RequiredArgsConstructor
public class Client {

    private final Identification identification;

    private final SseEmitter emitter;

    private final Scheduler scheduler;

    public void scheduleBroadcast(final TimeInterval interval, final Runnable runnable) {
        scheduler.schedule(interval, runnable);
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

        if (data == null) {
            return;
        }

        SseEmitter.SseEventBuilder sseBuilder = SseEmitter.event()
                .name(emitter.toString())
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            log.error("message send error {}", e.getMessage());
        }
    }

}
