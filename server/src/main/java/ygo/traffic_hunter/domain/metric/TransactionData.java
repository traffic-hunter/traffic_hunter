package ygo.traffic_hunter.domain.metric;

import java.time.Instant;
import lombok.Builder;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Builder
public record TransactionData(

        String name,

        String traceId,

        String parentSpanId,

        String spanId,

        Instant startTime,

        Instant endTime,

        long duration,

        String exception,

        boolean ended
) {

    public static TransactionData from(final TraceInfo traceInfo) {
        return TransactionData.builder()
                .name(traceInfo.name())
                .traceId(traceInfo.traceId())
                .parentSpanId(traceInfo.parentSpanId())
                .spanId(traceInfo.spanId())
                .startTime(traceInfo.startTime())
                .endTime(traceInfo.endTime())
                .duration(traceInfo.duration())
                .exception(traceInfo.exception())
                .ended(traceInfo.ended())
                .build();
    }
}
