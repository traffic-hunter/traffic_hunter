package org.traffichunter.javaagent.plugin.spring.business.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.lang.reflect.Method;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

public class SpringBusinessInstrumentationHelper {

    private static final String SPRING_BUSINESS_TRACE_SCOPE = "spring-business-trace";

    public static class Service {

        public static SpanScope start(final Method method, final Context parentContext) {
            Span span = TraceManager.getTracer(SPRING_BUSINESS_TRACE_SCOPE)
                    .spanBuilder("spring-business-service-span")
                    .setParent(parentContext)
                    .setAttribute("method.name", method.getName())
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class Repository {

        public static SpanScope start(final Method method, final Context parentContext) {

            Span span = TraceManager.getTracer(SPRING_BUSINESS_TRACE_SCOPE)
                    .spanBuilder("spring-business-repository-span")
                    .setParent(parentContext)
                    .setAttribute("method.name", method.getName())
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
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
}
