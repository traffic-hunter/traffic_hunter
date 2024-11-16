package ygo.traffic_hunter.presentation.response.systeminfo;

import java.time.Instant;
import ygo.traffic_hunter.presentation.response.systeminfo.cpu.CpuStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.memory.MemoryStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        Instant time,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo
) {
}
