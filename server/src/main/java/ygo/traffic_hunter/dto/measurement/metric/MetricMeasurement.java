package ygo.traffic_hunter.dto.measurement.metric;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import ygo.traffic_hunter.dto.measurement.metadata.Metadata;
import ygo.traffic_hunter.dto.measurement.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.thread.ThreadMetricMeasurement;

@Measurement(name = "metric")
public record MetricMeasurement(

        @Column(name = "metadata")
        Metadata metadata,

        @Column(name = "time", timestamp = true)
        Instant time,

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
