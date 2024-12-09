package ygo.traffic_hunter.core.dto.request.systeminfo.thread;

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
