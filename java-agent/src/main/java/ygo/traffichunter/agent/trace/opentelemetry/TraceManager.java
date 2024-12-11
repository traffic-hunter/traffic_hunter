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
