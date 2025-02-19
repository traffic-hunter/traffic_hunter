package ygo.traffic_hunter.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.AlarmRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

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

}