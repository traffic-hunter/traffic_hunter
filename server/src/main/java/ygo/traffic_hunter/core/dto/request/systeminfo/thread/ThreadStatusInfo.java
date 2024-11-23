package ygo.traffic_hunter.core.dto.request.systeminfo.thread;

public record ThreadStatusInfo(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
