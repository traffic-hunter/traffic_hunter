/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.extension.otel;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
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

        Map<String, String> attributes,

        int attributesCount,

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

        Map<String, String> map = AttributesParser.doParse(spanData.getAttributes());

        return new TraceInfo(
                spanData.getName(),
                spanData.getTraceId(),
                spanData.getParentSpanId(),
                spanData.getSpanId(),
                map,
                map.size(),
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
