package ygo.traffichunter.agent.engine.metric.transaction;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

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

    public static TraceInfo translate(final SpanData spanData) {

        Instant startTime = Instant.EPOCH.plusNanos(spanData.getStartEpochNanos());

        Instant endTime = Instant.EPOCH.plusNanos(spanData.getEndEpochNanos());

        Duration between = Duration.between(startTime, endTime);

        return new TraceInfo(
                spanData.getName(),
                spanData.getTraceId(),
                spanData.getParentSpanId(),
                spanData.getSpanId(),
                startTime,
                endTime,
                between.toMillis(),
                getException(spanData),
                spanData.hasEnded()
        );
    }

    private static String getException(final SpanData spanData) {
        if(Objects.equals(spanData.getStatus().getStatusCode(), StatusCode.ERROR)) {
            return spanData.getStatus().getDescription();
        }

        return "success";
    }
}
