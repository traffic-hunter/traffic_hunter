package ygo.traffic_hunter.core.dto.request.alarm;

public record AlarmThreshold(
        int cpuThreshold,

        int memoryThreshold,

        int threadThreshold,

        int webRequestThreshold,

        int webThreadThreshold,

        int dbcpThreshold
) {
}
