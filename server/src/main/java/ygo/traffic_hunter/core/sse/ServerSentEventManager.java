/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
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

        log.info("schedule broadcasting sse emitter {}", timeInterval);

        Client client = clients.get(identification);
        client.scheduleBroadcast(timeInterval, runnable);
    }

    public <T> void send(final Identification identification, final T data) {

        Client client = clients.get(identification);
        client.send(data);
    }

    static class ServerSentEventException extends RuntimeException {

        public ServerSentEventException() {
        }

        public ServerSentEventException(final String message) {
            super(message);
        }

        public ServerSentEventException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public ServerSentEventException(final Throwable cause) {
            super(cause);
        }

        public ServerSentEventException(final String message, final Throwable cause, final boolean enableSuppression,
                                        final boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
