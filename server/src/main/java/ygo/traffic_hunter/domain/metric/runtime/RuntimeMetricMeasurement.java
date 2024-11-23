package ygo.traffic_hunter.domain.metric.runtime;

public record RuntimeMetricMeasurement(

        long getStartTime,

        long getUpTime,

        String getVmName,

        String getVmVersion
) {
}
