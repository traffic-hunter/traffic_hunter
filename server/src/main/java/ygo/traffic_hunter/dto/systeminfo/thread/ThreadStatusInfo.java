package ygo.traffic_hunter.dto.systeminfo.thread;

public record ThreadStatusInfo(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
