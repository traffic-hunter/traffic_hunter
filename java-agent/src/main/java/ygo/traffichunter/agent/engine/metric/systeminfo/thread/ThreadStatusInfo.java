package ygo.traffichunter.agent.engine.metric.systeminfo.thread;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record ThreadStatusInfo(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
