package ygo.traffic_hunter.core.dto.response.alarm;

public record ThresholdResponse(

        int cpuThreshold,

        int memoryThreshold,

        int threadThreshold,

        int webRequestThreshold,

        int webThreadThreshold,

        int dbcpThreshold
) {
}
