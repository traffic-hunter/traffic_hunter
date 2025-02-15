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
package ygo.traffic_hunter.core.webhook.discord;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebHook implements AlarmSender {

    private final RestClient restClient;

    private final WebHookProperties properties;

    private final AtomicBoolean isActive = new AtomicBoolean(true);

    @Override
    public void enable() {
        this.isActive.compareAndSet(false, true);
    }

    @Override
    public void disable() {
        this.isActive.compareAndSet(true, false);
    }

    @Override
    public boolean isActive() {
        return this.isActive.get();
    }

    @Override
    public boolean isWebHook() {
        return true;
    }

    @Override
    public void send(final Message message) {

        restClient.post()
                .uri(URI.create(properties.discordUrl()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(message)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) ->
                        log.info("status code = {} status = {}", response.getStatusCode(), response.getStatusText())))
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        log.error("status code = {} status = {}", response.getStatusCode(), response.getStatusText()))
                .toBodilessEntity();
    }
}
