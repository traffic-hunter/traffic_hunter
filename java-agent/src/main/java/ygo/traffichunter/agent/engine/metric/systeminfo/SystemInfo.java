package ygo.traffichunter.agent.engine.metric.systeminfo;

import java.time.Instant;
import ygo.traffichunter.agent.engine.metric.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.metric.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        Instant time,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo
) {
}
