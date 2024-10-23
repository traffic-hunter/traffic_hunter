package ygo.traffichunter.agent.engine.systeminfo;

import java.time.Instant;
import java.time.LocalDateTime;
import ygo.traffichunter.agent.engine.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        Instant time,
        String targetJVM,
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo,
        GarbageCollectionStatusInfo garbageCollectionStatusInfo,
        RuntimeStatusInfo runtimeStatusInfo
) {
}
