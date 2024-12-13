package ygo.traffic_hunter.common.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.common.map.impl.system.SystemInfoMapperImpl;
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
import ygo.traffic_hunter.core.repository.AgentRepository;
import ygo.traffic_hunter.domain.entity.Agent;
import ygo.traffic_hunter.domain.entity.MetricMeasurement;

@ExtendWith(MockitoExtension.class)
public class SystemInfoMapperTest extends AbstractTestConfiguration {

    @InjectMocks
    private SystemInfoMapperImpl mapper;

    @Mock
    private AgentRepository agentRepository;

    @Test
    void metadataWrapper를_measurement로_변환한다() {
        // given
        AgentMetadata metadata = new AgentMetadata(
                "test-agent-id",
                "1.0",
                "Test Agent",
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
                        new TomcatRequestInfo(1, 1, 1, 1, 1)
                ),
                new HikariDbcpInfo(1, 1, 1, 1)
        );

        MetadataWrapper<SystemInfo> metadataWrapper = new MetadataWrapper<>(metadata, systemInfo);

        Agent mockAgent = new Agent(
                1, // ID
                "test-agent-id",
                "Test Agent",
                "1.0",
                Instant.now()
        );

        given(agentRepository.findByAgentId(metadata.agentId())).willReturn(mockAgent);

        // when
        MetricMeasurement measurement = mapper.map(metadataWrapper);

        // then
        assertNotNull(measurement);
        assertEquals(mockAgent.id(), measurement.agentId());
    }
}