package ygo.traffic_hunter.dto.measurement.metric;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import ygo.traffic_hunter.dto.measurement.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.dto.measurement.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.dto.systeminfo.metadata.AgentStatus;

@Measurement(name = "metric")
public record MetricMeasurement(

        @Column(name = "agent_id", tag = true)
        String agentId,

        @Column(name = "agent_name", tag = true)
        String agentName,

        @Column(name = "agent_version")
        String agentVersion,

        @Column(name = "agent_boot_time")
        Instant agentBootTime,

        @Column(name = "time", timestamp = true)
        Instant time,

        @Column(name = "cpu_metric", tag = true)
        CpuMetricMeasurement cpuMetric,

        @Column(name = "gc_metric", tag = true)
        GCMetricMeasurement gcMetric,

        @Column(name = "memory_metric", tag = true)
        MemoryMetricMeasurement memoryMetric,

        @Column(name = "runtime_metric", tag = true)
        RuntimeMetricMeasurement runtimeMetric,

        @Column(name = "thread_metric", tag = true)
        ThreadMetricMeasurement threadMetric
) {
}
