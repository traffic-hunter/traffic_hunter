package ygo.traffichunter.agent.engine.metric.systeminfo;

import java.time.Instant;
import ygo.traffichunter.agent.engine.metric.dbcp.HikariDbcpInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.thread.ThreadStatusInfo;
import ygo.traffichunter.agent.engine.metric.web.tomcat.TomcatWebServerInfo;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record SystemInfo(
        Instant time,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo,
        TomcatWebServerInfo tomcatWebServerInfo,
        HikariDbcpInfo hikariDbcpInfo
) {
}
