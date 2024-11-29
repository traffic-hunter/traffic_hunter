package ygo.traffic_hunter.core.dto.request.systeminfo;

import java.time.Instant;
import ygo.traffic_hunter.core.dto.request.Metric;
import ygo.traffic_hunter.core.dto.request.systeminfo.dbcp.HikariDbcpInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.thread.ThreadStatusInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.web.tomcat.TomcatWebServerInfo;

public record SystemInfo(
        Instant time,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo,
        TomcatWebServerInfo tomcatWebServerInfo,
        HikariDbcpInfo hikariDbcpInfo
) {}
