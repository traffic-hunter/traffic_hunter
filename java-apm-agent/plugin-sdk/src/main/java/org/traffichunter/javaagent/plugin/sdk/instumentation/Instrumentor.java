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
package org.traffichunter.javaagent.plugin.sdk.instumentation;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.util.Objects;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class Instrumentor {

    private static final String INSTRUMENTATION_NAME = "traffichunter.instrumentation";

    Instrumentor() {}

    @FunctionalInterface
    public interface SpanNameGenerator<OPERATION> {

        String generate(OPERATION operation);
    }

    @FunctionalInterface
    public interface SpanAttributeSupplier<OPERATION> {

        void get(Span span, OPERATION operation);
    }

    public static <OPERATION> InstrumentationStartBuilder<OPERATION> startBuilder(final OPERATION operation) {
        return new InstrumentationStartBuilder<>(operation);
    }

    public static <OPERATION> InstrumentationEndBuilder<OPERATION> endBuilder(final OPERATION operation) {
        return new InstrumentationEndBuilder<>(operation);
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {

        Span span = spanScope.span();
        Scope scope = spanScope.scope();

        if (throwable != null) {
            span.recordException(throwable);
            span.setStatus(StatusCode.ERROR, throwable.getMessage());
        }

        span.end();
        scope.close();
    }

    public static final class InstrumentationEndBuilder<OPERATION> {

        private final OPERATION operation;

        private SpanScope spanScope;

        private SpanAttributeSupplier<OPERATION> spanAttributeSupplier;

        private Throwable throwable;

        InstrumentationEndBuilder(final OPERATION operation) {
            this.operation = operation;
            this.spanAttributeSupplier = (span, oper) -> {};
        }

        public InstrumentationEndBuilder<OPERATION> spanAttribute(final SpanAttributeSupplier<OPERATION> supplier) {
            this.spanAttributeSupplier = supplier;
            return this;
        }

        public InstrumentationEndBuilder<OPERATION> spanScope(final SpanScope spanScope) {
            this.spanScope = spanScope;
            return this;
        }

        public InstrumentationEndBuilder<OPERATION> throwable(final Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public void end() {
            Span span = spanScope.span();
            Scope scope = spanScope.scope();
            spanAttributeSupplier.get(span, operation);

            if (throwable != null) {
                span.recordException(throwable);
                span.setStatus(StatusCode.ERROR, throwable.getMessage());
            }

            span.end();
            scope.close();
        }
    }

    public static final class InstrumentationStartBuilder<OPERATION> {

        private final OPERATION operation;

        private String spanName;

        private Context parentContext;

        private SpanAttributeSupplier<OPERATION> spanAttributeSupplier;

        InstrumentationStartBuilder(final OPERATION operation) {
            this.operation = operation;
            this.spanAttributeSupplier = (span, oper) -> {};
        }

        public InstrumentationStartBuilder<OPERATION> spanName(final String name) {
            this.spanName = name;
            return this;
        }

        public InstrumentationStartBuilder<OPERATION> spanName(final SpanNameGenerator<OPERATION> spanNameGenerator) {
            this.spanName = spanNameGenerator.generate(operation);
            return this;
        }

        public InstrumentationStartBuilder<OPERATION> context(final Context parentContext) {
            this.parentContext = parentContext;
            return this;
        }

        public InstrumentationStartBuilder<OPERATION> spanAttribute(final SpanAttributeSupplier<OPERATION> supplier) {
            this.spanAttributeSupplier = supplier;
            return this;
        }

        public SpanScope start() {

            String spanNamer = Objects.requireNonNullElseGet(spanName, () -> operation.getClass().getSimpleName());

            Tracer tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_NAME);

            SpanBuilder spanBuilder = tracer.spanBuilder(spanNamer);

            if(parentContext != null) {
                spanBuilder.setParent(parentContext);
            }

            Span span = spanBuilder.startSpan();
            if(spanAttributeSupplier != null) {
                spanAttributeSupplier.get(span, operation);
            }

            return SpanScope.create(span, span.makeCurrent());
        }
    }
}
