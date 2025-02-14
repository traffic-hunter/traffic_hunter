package org.traffichunter.javaagent.plugin.spring.business.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.lang.reflect.Method;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

public class SpringBusinessInstrumentationHelper {

    private static final String SPRING_BUSINESS_SERVICE_TRACE_SCOPE = "spring-business-service-trace";

    private static final String SPRING_BUSINESS_REPOSITORY_TRACE_SCOPE = "spring-business-repository-trace";

    public static class Service {

        public static SpanScope start(final Method method, final Context parentContext) {

            Span span = TraceManager.getTracer(SPRING_BUSINESS_SERVICE_TRACE_SCOPE)
                    .spanBuilder(generateSpanName(method))
                    .setParent(parentContext)
                    .startSpan();

            return new SpanScope(span, span.makeCurrent());
        }
    }

    public static class Repository {

        public static SpanScope start(final Method method, final Context parentContext) {

            Span span = TraceManager.getTracer(SPRING_BUSINESS_REPOSITORY_TRACE_SCOPE)
                    .spanBuilder(generateSpanName(method))
                    .setParent(parentContext)
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

    private static String generateSpanName(final Method method) {

        try {
            return method.getName().split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return method.getName();
        }
    }
}
