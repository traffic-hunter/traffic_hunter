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
package org.traffichunter.javaagent.trace.manager;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.Objects;
import org.traffichunter.javaagent.trace.exporter.TraceExporter;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class TraceManager {

    private static final OpenTelemetrySdk openTelemetrySdk;

    static {
        SdkTracerProvider provider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(new TraceExporter()))
                .setIdGenerator(IdGenerator.random())
                .setSampler(Sampler.alwaysOn())
                .build();

        openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(provider)
                .buildAndRegisterGlobal();
    }

    public static Tracer getTracer(final String instrumentationName) {
        if(Objects.isNull(openTelemetrySdk)) {
            throw new IllegalStateException("OpenTelemetry is not configured. Call configure() first.");
        }

        return openTelemetrySdk.getTracer(instrumentationName);
    }

    public static void close() {
        openTelemetrySdk.close();
    }

    public record SpanScope(Span span, Scope scope) {

        public static final SpanScope NOOP = new SpanScope(Span.current(), Scope.noop());
    }
}
