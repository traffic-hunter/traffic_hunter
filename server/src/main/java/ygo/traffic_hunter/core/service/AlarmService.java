package ygo.traffic_hunter.core.service;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.AlarmRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final AlarmManger alarmManger;

    public ThresholdResponse retrieveThreshold() {

        return alarmRepository.findThreshold();
    }

    @Transactional
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

    public void updateThreshold(final AlarmThreshold alarmThreshold) {
        Threshold threshold = Threshold.builder()
                .cpuThreshold(alarmThreshold.cpuThreshold())
                .memoryThreshold(alarmThreshold.memoryThreshold())
                .webRequestThreshold(alarmThreshold.webRequestThreshold())
                .webThreadThreshold(alarmThreshold.webThreadThreshold())
                .dbcpThreshold(alarmThreshold.dbcpThreshold())
                .build();
        alarmManger.updateThreshold(threshold);

    }

}