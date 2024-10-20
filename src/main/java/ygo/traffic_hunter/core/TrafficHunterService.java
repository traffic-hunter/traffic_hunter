package ygo.traffic_hunter.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ygo.traffic_hunter.common.aop.lock.Lock;
import ygo.traffic_hunter.common.aop.lock.method.LockMode;
import ygo.traffic_hunter.core.repository.MetricRepository;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;

@Service
@RequiredArgsConstructor
public class TrafficHunterService {

    private final MetricRepository metricRepository;

    @Lock(mode = LockMode.WRITE)
    public void save(final SystemInfo systemInfo) {

    }

}
