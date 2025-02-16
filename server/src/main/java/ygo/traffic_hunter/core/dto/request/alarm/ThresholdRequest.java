package ygo.traffic_hunter.core.dto.request.alarm;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ThresholdRequest(

        @Min(0)
        @Max(100)
        int cpuThreshold,

        @Min(0)
        @Max(100)
        int memoryThreshold,

        @Min(0)
        @Max(100)
        int threadThreshold,

        @Min(0)
        @Max(Integer.MAX_VALUE - 1)
        int webRequestThreshold,

        @Min(0)
        @Max(100)
        int webThreadThreshold,

        @Min(0)
        @Max(100)
        int dbcpThreshold
) {
}
