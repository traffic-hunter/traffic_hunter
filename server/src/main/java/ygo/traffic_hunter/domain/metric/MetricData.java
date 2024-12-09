package ygo.traffic_hunter.domain.metric;

import ygo.traffic_hunter.domain.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.metric.dbcp.hikari.HikariCPMeasurement;
import ygo.traffic_hunter.domain.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.domain.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.TomcatWebServerMeasurement;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record MetricData(
        CpuMetricMeasurement cpuMetric,

        GCMetricMeasurement gcMetric,

        MemoryMetricMeasurement memoryMetric,

        RuntimeMetricMeasurement runtimeMetric,

        ThreadMetricMeasurement threadMetric,

        TomcatWebServerMeasurement webServerMetric,

        HikariCPMeasurement dbcpMetric
) {
}
