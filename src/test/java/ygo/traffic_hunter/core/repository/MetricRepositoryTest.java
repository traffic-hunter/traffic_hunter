package ygo.traffic_hunter.core.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.common.map.DataToMeasurementMapper;
import ygo.traffic_hunter.domain.measurement.metric.MetricMeasurement;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.presentation.response.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.thread.ThreadStatusInfo;

@SpringBootTest
class MetricRepositoryTest {

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private DataToMeasurementMapper mapper;

    @Test
    void DB에_잘_저장이_되는지_확인한다() {
        SystemInfo systemInfo = new SystemInfo(
                Instant.now(),
                new MemoryStatusInfo(
                        new MemoryStatusInfo.MemoryUsage(1000L, 500L, 800L, 1024L),
                        new MemoryStatusInfo.MemoryUsage(500L, 200L, 400L, 512L)
                ),
                new ThreadStatusInfo(10, 15, 100L),
                new CpuStatusInfo(0.5, 0.3, 4),
                new GarbageCollectionStatusInfo(Collections.singletonList(
                        new GarbageCollectionTime(5L, 100L)
                )),
                new RuntimeStatusInfo(1000L, 5000L, "TestVM", "1.0")
        );

        // when
        MetricMeasurement result = mapper.systemInfoToMetricMeasurement(systemInfo);

        metricRepository.save(result);
    }
}