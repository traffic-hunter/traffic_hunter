package ygo.traffichunter.engine.systeminfo;

import ygo.traffichunter.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.engine.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo
) {
}
