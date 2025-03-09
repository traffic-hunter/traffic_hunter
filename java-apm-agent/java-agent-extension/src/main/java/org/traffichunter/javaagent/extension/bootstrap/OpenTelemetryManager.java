/*
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
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
package org.traffichunter.javaagent.extension.bootstrap;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.List;
import org.traffichunter.javaagent.bootstrap.OpenTelemetrySdkBridge;
import org.traffichunter.javaagent.extension.otel.TrafficHunterExporter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class OpenTelemetryManager {

    private OpenTelemetryManager() {}

    static final SpanExporter spanExporter = new TrafficHunterExporter();

    static OpenTelemetrySdk manageOpenTelemetrySdk() {

        SdkTracerProvider provider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                .setIdGenerator(IdGenerator.random())
                .setSampler(Sampler.alwaysOn())
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(provider)
                .buildAndRegisterGlobal();

        OpenTelemetrySdkBridge.setOpenTelemetrySdkForceFlush(((timeout, unit) -> {
            CompletableResultCode traceProvider = openTelemetrySdk.getSdkTracerProvider().forceFlush();
            CompletableResultCode metricProvider = openTelemetrySdk.getSdkMeterProvider().forceFlush();
            CompletableResultCode logProvider = openTelemetrySdk.getSdkLoggerProvider().forceFlush();
            CompletableResultCode
                    .ofAll(List.of(traceProvider, metricProvider, logProvider))
                    .join(timeout, unit);
        }));

        return openTelemetrySdk;
    }

    public static void flush() {
        spanExporter.flush();
    }
}
