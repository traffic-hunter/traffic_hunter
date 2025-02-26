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
package ygo.traffic_hunter.core.webhook.slack;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.alarm.WebHookAlarm;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.alarm.message.library.MessageMaker.Color;
import ygo.traffic_hunter.core.webhook.Webhook;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlackWebHook implements WebHookAlarm, AlarmSender {

    private final Slack slack;

    private final WebHookProperties properties;

    private final AtomicBoolean isActive = new AtomicBoolean(true);

    @Override
    public Webhook getWebhook() {
        return Webhook.SLACK;
    }

    @Override
    public void enable() {

        if(isActive.get()) {
            return;
        }

        isActive.compareAndSet(false, true);
    }

    @Override
    public void disable() {

        if(!isActive.get()) {
            return;
        }

        isActive.compareAndSet(true, false);
    }

    @Override
    public boolean isActive() {
        return isActive.get();
    }

    @Override
    public void send(final Message message) {

        if(!isActive.get() || properties.discordUrl() == null || properties.discordUrl().isEmpty()) {
            return;
        }

        try {
            WebhookResponse response = slack.send(
                    properties.slackUrl(),
                    Payload.builder()
                            .attachments(Collections.singletonList(getAttachment(message)))
                            .build()
            );

            HttpStatus httpStatus = HttpStatus.valueOf(response.getCode());

            if(httpStatus.is2xxSuccessful()) {
                log.info("http status code = {}", httpStatus.value());
            } else if(httpStatus.is4xxClientError() || httpStatus.is5xxServerError()) {
                log.error("http status code = {}", httpStatus.value());
            }
        } catch (IOException e) {
            throw new AlarmException(e.getMessage(), e);
        }
    }

    private Attachment getAttachment(final Message message) {

        return Attachment.builder()
                .color(Color.RED.getStringValue())
                .authorName(message.username())
                .title(message.content())
                .fields(getFields(message))
                .build();
    }

    private List<Field> getFields(final Message message) {

        return message.embeds()
                .getFirst()
                .fields()
                .stream()
                .map(field -> Field.builder()
                        .title(field.name())
                        .value(field.value())
                        .build()
                ).toList();
    }
}
