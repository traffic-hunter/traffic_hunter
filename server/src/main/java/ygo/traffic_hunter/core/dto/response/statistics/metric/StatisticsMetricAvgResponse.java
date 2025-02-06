package ygo.traffic_hunter.core.dto.response.statistics.metric;

import java.time.Instant;

public record StatisticsMetricAvgResponse(

        Instant time,

        double avgSystemCpuUsage,
        double avgProcessCpuUsage,

        double avgHeapMemoryUsage,

        double avgThreadCount,
        double avgPeakThreadCount,

        double avgWebRequestCount,
        double avgWebErrorCount,
        double avgWebThreadCount,

        double avgDbConnectionCount
) {
}
