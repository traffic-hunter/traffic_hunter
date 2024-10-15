package ygo.traffichunter.agent.engine.systeminfo;

import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;

public record SystemInfo(
        MemoryStatusInfo memoryStatusInfo,
        ThreadStatusInfo threadStatusInfo,
        CpuStatusInfo cpuStatusInfo
) {
}
