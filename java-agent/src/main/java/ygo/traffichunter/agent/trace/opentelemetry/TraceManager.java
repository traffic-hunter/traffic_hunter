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
package ygo.traffichunter.agent.trace.opentelemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.Objects;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class TraceManager {

    private static final String INSTRUMENTATION_SCOPE_NAME = "instrument-transaction-scope";

    private static volatile SdkTracerProvider provider;

    private static volatile OpenTelemetrySdk openTelemetrySdk;

    public static Tracer configure(final SpanExporter exporter) {

        provider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                .setIdGenerator(IdGenerator.random())
                .setSampler(Sampler.alwaysOn())
                .build();

        openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(provider)
                .build();

        return openTelemetrySdk.getTracer(INSTRUMENTATION_SCOPE_NAME);
    }

    public static void close() {
        if (Objects.nonNull(provider) && Objects.nonNull(openTelemetrySdk)) {
            provider.close();
            openTelemetrySdk.close();
        }
    }

    public record SpanScope(Span span, Scope scope) { }
}
