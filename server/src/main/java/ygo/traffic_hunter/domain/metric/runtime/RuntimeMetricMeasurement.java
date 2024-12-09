package ygo.traffic_hunter.domain.metric.runtime;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record RuntimeMetricMeasurement(

        long getStartTime,

        long getUpTime,

        String getVmName,

        String getVmVersion
) {
}
