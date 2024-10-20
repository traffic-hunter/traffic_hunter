package ygo.traffic_hunter.domain.measurement.metric;

import java.time.Instant;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import ygo.traffic_hunter.domain.measurement.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.domain.measurement.metric.thread.ThreadMetricMeasurement;

@Measurement(name = "metric")
public record MetricMeasurement(

        @TimeColumn
        @Column(name = "time")
        Instant time,

        @Column(name = "client_ip")
        String clientIp,

        @Column(name = "cpu_metric")
        CpuMetricMeasurement cpuMetric,

        @Column(name = "gc_metric")
        GCMetricMeasurement gcMetric,

        @Column(name = "memory_metric")
        MemoryMetricMeasurement memoryMetric,

        @Column(name = "runtime_metric")
        RuntimeMetricMeasurement runtimeMetric,

        @Column(name = "thread_metric")
        ThreadMetricMeasurement threadMetric
) {
}
