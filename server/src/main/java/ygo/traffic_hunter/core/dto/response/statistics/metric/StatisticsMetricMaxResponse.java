package ygo.traffic_hunter.core.dto.response.statistics.metric;

import java.time.Instant;

public record StatisticsMetricMaxResponse(

        Instant time,

        double maxSystemCpuUsage,
        double maxProcessCpuUsage,

        double maxHeapMemoryUsage,

        int maxThreadCount,
        int maxPeakThreadCount,

        int maxWebRequestCount,
        int maxWebErrorCount,
        int maxWebThreadCount,

        int maxDbConnectionCount
) {
}
