package ygo.traffic_hunter.presentation.response.systeminfo.thread;

public record ThreadStatusInfo(
        int threadCount,
        int getPeekThreadCount,
        long getTotalStartThreadCount
) {
}
