package ygo.traffic_hunter.domain.measurement.metric.runtime;

import org.influxdb.annotation.Column;

public record RuntimeMetricMeasurement(

        @Column(name = "start_time")
        long getStartTime,

        @Column(name = "up_time")
        long getUpTime,

        @Column(name = "vm_name")
        String getVmName,

        @Column(name = "vm_version")
        String getVmVersion
) {
}
