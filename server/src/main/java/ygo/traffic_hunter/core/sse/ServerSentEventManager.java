/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 traffic-hunter.org
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.schedule.Scheduler;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.send.ViewSender;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.domain.entity.user.Member;
import ygo.traffic_hunter.domain.interval.TimeInterval;

/**
 * @author JuSeong
 * @version 1.1.0
 */

@Slf4j
@Component
public class ServerSentEventManager implements AlarmSender, ViewSender {

    private final Map<Member, Client> clientMap = new ConcurrentHashMap<>();

    public SseEmitter register(final Member member, final SseEmitter emitter) {

        log.info("registering sse emitter {}", emitter);

        Client client = new Client(emitter, new Scheduler(Executors.newSingleThreadScheduledExecutor()));

        clientMap.put(member, client);

        client.send("connect", member.getEmail());

        emitter.onCompletion(() -> {
            log.info("completed sse emitter {}", emitter);
            clientMap.remove(member);
        });

        emitter.onTimeout(() -> {
            log.info("timed out sse emitter {}", emitter);
            emitter.complete();
        });

        return emitter;
    }

    public void scheduleBroadcast(final Member member,
                                  final TimeInterval timeInterval,
                                  final Runnable runnable) {

        if (!clientMap.containsKey(member)) {
            throw new IllegalStateException(
                    "The client with the given identification does not exist. Please subscribe first.");
        }

        log.info("schedule broadcasting sse emitter {}", timeInterval);

        Client client = clientMap.get(member);
        client.scheduleBroadcast(runnable);
    }

    @Override
    public void send(final Message message) {
        this.sendAll(message);
    }

    @Override
    public <T> void send(final T data) {
        this.sendAll(data);
    }

    @Override
    public <T> void send(final Member member, final T data) {
        Client client = clientMap.get(member);
        client.send(data, member.getEmail());
    }

    @Override
    public <T> void send(final List<T> data) {
        this.sendAll(data);
    }

    private <T> void sendAll(final T data) {

        for (Member member : clientMap.keySet()) {
            Client client = clientMap.get(member);

            if (member.isAlarm()) {
                client.send(data, member.getEmail());
            }
        }
    }

    private <T> void asyncSend(final T data) {

        clientMap.keySet().forEach(member -> {
            Client client = clientMap.get(member);

            CompletableFuture.runAsync(() -> client.send(data, member.getEmail()));
        });
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
