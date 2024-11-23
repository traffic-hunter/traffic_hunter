package ygo.traffic_hunter.core.repository;

import com.influxdb.query.FluxRecord;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.persistence.impl.TimeSeriesRepository;

@SpringBootTest
class MetricRepositoryTest {

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;

    @Autowired
    private SystemInfoMapper mapper;

    @AfterEach
    void init() {
        timeSeriesRepository.clear();
    }

    @Test
    void DB에_잘_저장이_되는지_확인한다() {
        // given
        AgentMetadata metadata = new AgentMetadata(
          "test",
          "test",
          "test",
          Instant.now(),
          AgentStatus.RUNNING
        );

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

        MetadataWrapper<SystemInfo> metadataWrapper = new MetadataWrapper<>(metadata, systemInfo);

        timeSeriesRepository.save(mapper.map(metadataWrapper));
        timeSeriesRepository.save(mapper.map(metadataWrapper));
        timeSeriesRepository.save(mapper.map(metadataWrapper));
        timeSeriesRepository.save(mapper.map(metadataWrapper));
        timeSeriesRepository.save(mapper.map(metadataWrapper));

        // when

        // then
    }
}