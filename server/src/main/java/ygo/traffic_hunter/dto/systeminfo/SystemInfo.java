package ygo.traffic_hunter.dto.systeminfo;

import java.time.Instant;
import ygo.traffic_hunter.dto.Metric;
import ygo.traffic_hunter.dto.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.dto.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.dto.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.dto.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.dto.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        Instant time,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo
) implements Metric {}
