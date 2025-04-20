package ygo.traffic_hunter.core.webhook.slack;

import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
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
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.alarm.message.MessageType;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

@Disabled
@EnableConfigurationProperties(WebHookProperties.class)
@SpringBootTest
class SlackWebHookTest extends AbstractTestConfiguration {

    @Autowired
    private SlackWebHook slackWebHook;

    @Test
    void slack_web_hook_이_정상적으로_동작한다() throws UnknownHostException {
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
                        new TomcatRequestInfo(1, 1, 1, 1, 1)
                ),
                new HikariDbcpInfo(1, 1, 1, 1)
        );

        MetadataWrapper<SystemInfo> metadataWrapper = new MetadataWrapper<>(metadata, systemInfo);

        Message message = MessageType.CPU.doMessage("http://localhost:8080/", metadataWrapper);

        // when
        slackWebHook.send(message);

        // then

    }
}