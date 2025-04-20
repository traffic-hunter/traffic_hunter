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
package ygo.traffic_hunter.core.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.alarm.WebHookAlarm;
import ygo.traffic_hunter.core.dto.response.alarm.ActivationWebhook;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.AlarmRepository;
import ygo.traffic_hunter.core.send.AlarmSender.AlarmException;
import ygo.traffic_hunter.core.webhook.Webhook;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final List<WebHookAlarm> webHookAlarms;

    @Cacheable(cacheNames = CacheType.THRESHOLD_CACHE_NAME, key = "'threshold'")
    public ThresholdResponse retrieveThreshold() {

        return alarmRepository.findThreshold();
    }

    @Transactional
    @CacheEvict(cacheNames = CacheType.THRESHOLD_CACHE_NAME, key = "'threshold'")
    public void updateThreshold(final int cpuThreshold,
                                final int memoryThreshold,
                                final int threadThreshold,
                                final int webRequestThreshold,
                                final int webThreadThreshold,
                                final int dbcpThreshold) {

        alarmRepository.updateThreshold(
                cpuThreshold,
                memoryThreshold,
                threadThreshold,
                webRequestThreshold,
                webThreadThreshold,
                dbcpThreshold
        );
    }

    public List<ActivationWebhook> getActiveWebhooks() {

        return webHookAlarms.stream()
                .map(webHookAlarm -> new ActivationWebhook(webHookAlarm.getWebhook(), webHookAlarm.isActive()))
                .toList();
    }

    public void enableWebhook(final Webhook webhook) {

        WebHookAlarm webHook = webHookAlarms.stream()
                .filter(webHookAlarm -> webhook == webHookAlarm.getWebhook())
                .findFirst()
                .orElseThrow(() -> new AlarmException("Webhook not found"));

        webHook.enable();
    }

    public void disableWebhook(final Webhook webhook) {

        WebHookAlarm webHook = webHookAlarms.stream()
                .filter(webHookAlarm -> webhook == webHookAlarm.getWebhook())
                .findFirst()
                .orElseThrow(() -> new AlarmException("Webhook not found"));

        webHook.disable();
    }
}