package ygo.traffic_hunter.domain.metric.memory.usage;

import org.influxdb.annotation.Column;

public record MemoryMetricUsage(

        @Column(name = "init")
        long init,

        @Column(name = "used")
        long used,

        @Column(name = "commited")
        long committed,

        @Column(name = "max")
        long max
) {
}
