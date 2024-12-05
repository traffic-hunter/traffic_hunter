package ygo.traffic_hunter.core.repository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.common.map.SystemInfoMapper;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.dto.request.metadata.AgentStatus;
import ygo.traffic_hunter.core.dto.request.metadata.MetadataWrapper;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.dbcp.HikariDbcpInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.collections.GarbageCollectionTime;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo.MemoryUsage;
import ygo.traffic_hunter.core.dto.request.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.TomcatWebServerInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.request.TomcatRequestInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.thread.TomcatThreadPoolInfo;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.domain.interval.TimeInterval;
import ygo.traffic_hunter.persistence.impl.TimeSeriesRepository;

@SpringBootTest
class MetricRepositoryTest extends AbstractTestConfiguration {

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;

    @Qualifier("systemInfoMapperImpl")
    @Autowired
    private SystemInfoMapper mapper;

    @AfterEach
    void init() {
        timeSeriesRepository.clear();
    }

    @Test
    void DB에_저장이_되는지_확인한다() {
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
                        new MemoryUsage(1000L, 500L, 800L, 1024L),
                        new MemoryUsage(500L, 200L, 400L, 512L)
                ),
                new ThreadStatusInfo(10, 15, 100L),
                new CpuStatusInfo(0.5, 0.3, 4),
                new GarbageCollectionStatusInfo(Collections.singletonList(
                        new GarbageCollectionTime(5L, 100L)
                )),
                new RuntimeStatusInfo(1000L, 5000L, "TestVM", "1.0"),
                new TomcatWebServerInfo(
                        new TomcatThreadPoolInfo(1, 1, 1),
                        new TomcatRequestInfo(1,1, 1, 1, 1)
                ),
                new HikariDbcpInfo(1,1, 1, 1)
        );

        MetadataWrapper<SystemInfo> metadataWrapper = new MetadataWrapper<>(metadata, systemInfo);

        timeSeriesRepository.save(mapper.map(metadataWrapper));

        // when
        List<SystemMetricResponse> metrics = timeSeriesRepository.findMetricsByRecentTimeAndAgentName(
                TimeInterval.TEN_MINUTES, "test");

        // then
        System.out.println(metrics);
        Assertions.assertThat(metrics).hasSize(1);
    }
}