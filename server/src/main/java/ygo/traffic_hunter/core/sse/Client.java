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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ygo.traffic_hunter.core.schedule.Scheduler;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.send.ViewSender;
import ygo.traffic_hunter.core.sse.ServerSentEventManager.ServerSentEventException;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.core.webhook.message.SseMessage;
import ygo.traffic_hunter.domain.entity.user.Member;

/**
 * @author yungwang-o, JuSeong
 * @version 1.1.0
 * @see SseEmitter
 */
@Slf4j
public class Client implements AlarmSender, ViewSender {

    private static final int DEFAULT_INTERVAL = 5000;

    private final Member member;

    private final SseEmitter emitter;

    private final Scheduler scheduler;

    private boolean isActive;

    public Client(final Member member, final SseEmitter emitter, final Scheduler scheduler) {
        this.member = member;
        this.emitter = emitter;
        this.scheduler = scheduler;
        this.isActive = member.isAlarm();
    }

    public void scheduleBroadcast(final Runnable runnable) {
        scheduler.schedule(DEFAULT_INTERVAL, runnable);
    }

    @Override
    public void enable() {
        this.isActive = true;
    }

    @Override
    public void disable() {
        this.isActive = false;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public boolean isWebHook() {
        // fix
        return false;
    }

    @Override
    public void send(final Message message) {
        SseMessage sseMessage = SseMessage.from(message);
        send(sseMessage, emitter);
    }

    @Override
    public <T> void send(final T data) {
        send(data, emitter);
    }

    @Override
    public <T> void send(final List<T> data) {
        send(data, emitter);
    }

    private <T> void send(final T data, final SseEmitter emitter) {

        if (data == null) {
            return;
        }

        SseEmitter.SseEventBuilder sseBuilder = SseEmitter.event()
                .name(String.valueOf(member.getId()))
                .data(data);

        try {
            emitter.send(sseBuilder);
        } catch (IOException e) {
            log.error("sse message send error {}", e.getMessage());
            throw new ServerSentEventException(e.getMessage(), e);
        }
    }

}
