package ygo.traffichunter.agent.engine.metric.systeminfo.thread;

import java.lang.management.ThreadInfo;

public record ThreadStatusInfo(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
