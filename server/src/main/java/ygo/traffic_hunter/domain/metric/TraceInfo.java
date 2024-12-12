package ygo.traffic_hunter.domain.metric;

import java.time.Instant;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public record TraceInfo(

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
}
