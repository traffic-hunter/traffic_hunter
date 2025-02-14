/**
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
package org.traffichunter.javaagent.plugin.spring.webmvc.helper;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.http.HttpStatus;
import org.traffichunter.javaagent.trace.manager.TraceManager;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class SpringWebMvcInstrumentationHelper {

    private static final String SPRING_WEBMVC_INSTRUMENTATION_SCOPE_NAME = "spring-webmvc-tracer";

    public static SpanScope start(final Method method,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) {

        Span span = TraceManager.getTracer(SPRING_WEBMVC_INSTRUMENTATION_SCOPE_NAME)
                .spanBuilder(generateSpanName(request))
                .setAttribute("method.name", method.getName())
                .setAttribute("http.method", request.getMethod())
                .setAttribute("http.url", request.getRequestURL().toString())
                .setAttribute("http.status", HttpStatus.valueOf(response.getStatus()).name().toUpperCase())
                .setAttribute("http.statusCode", response.getStatus())
                .setAttribute("http.requestURI", request.getRequestURI())
                .setAttribute("http.queryString", request.getQueryString())
                .setAttribute("http.serverName", request.getServerName())
                .setAttribute("http.serverPort", request.getServerPort())
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

    private static String generateSpanName(final HttpServletRequest request) {

        return request.getMethod() + " " + request.getRequestURI();
    }
}
