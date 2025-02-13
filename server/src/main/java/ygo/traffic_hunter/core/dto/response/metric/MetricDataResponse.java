package ygo.traffic_hunter.core.dto.response.metric;

public record MetricDataResponse(
        CpuMetricMeasurementResponse cpuMetric,

        MemoryMetricMeasurementResponse memoryMetric,

        ThreadMetricMeasurementResponse threadMetric,

        TomcatWebServerMeasurementResponse webServerMetric,

        HikariCPMeasurementResponse dbcpMetric
) {

}
