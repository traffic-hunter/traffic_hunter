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