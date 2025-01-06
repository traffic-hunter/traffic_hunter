package org.traffichunter.javaagent.plugin.spring.webmvc.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

public class SpringWebMvcInstrumentationHelper {

    private static final String SPRING_WEBMVC_INSTRUMENTATION_SCOPE_NAME = "spring-webmvc-tracer";

    public static SpanScope start(final Method method, final HttpServletRequest request) {

        Span span = TraceManager.getTracer(SPRING_WEBMVC_INSTRUMENTATION_SCOPE_NAME)
                .spanBuilder(method.getName())
                .setAttribute("http.method", request.getMethod())
                .setAttribute("http.requestURI", request.getRequestURI())
                .startSpan();

        request.setAttribute("dispatch.span", span);

        return new SpanScope(span, span.makeCurrent());
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
