/*
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
package ygo.traffic_hunter.core.alarm.loss.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.core.alarm.loss.LossPreventionHooker;
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.alarm.message.SseMessage;
import ygo.traffic_hunter.core.repository.AlarmRepository;
import ygo.traffic_hunter.core.send.AlarmSender.AlarmException;
import ygo.traffic_hunter.domain.entity.alarm.DeadLetter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmPersistenceLossPreventionHooker implements LossPreventionHooker {

    private final AlarmRepository alarmRepository;

    @Override
    @Transactional
    public <T> void hook(final T lossMessage) {

        try {
            log.info("message {}", lossMessage);
            alarmRepository.save(new DeadLetter(getMessage(lossMessage)));
        } catch (JsonProcessingException e) {
            throw new AlarmException("Failed to serialize message", e);
        }
    }

    private <T> Message getMessage(final T lossMessage) {

        if (lossMessage instanceof SseMessage sseMessage) {
            return sseMessage.message();
        }
        return (Message) lossMessage;
    }
}
