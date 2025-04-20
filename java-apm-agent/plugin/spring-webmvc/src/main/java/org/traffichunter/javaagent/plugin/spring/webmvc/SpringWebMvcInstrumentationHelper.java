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
package org.traffichunter.javaagent.plugin.spring.webmvc;

import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.springframework.http.HttpStatus;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class SpringWebMvcInstrumentationHelper {

    public static SpanScope start(final Method method,
                                  final Context parentContext,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) {

        return Instrumentor.startBuilder(request)
                .instrumentationName("spring-web-mvc-inst")
                .spanName(SpringWebMvcInstrumentationHelper::generateSpanName)
                .context(parentContext)
                .spanAttribute((span, req) ->
                        span.setAttribute("method.name", method.getName())
                        .setAttribute("http.method", request.getMethod())
                        .setAttribute("http.url", request.getRequestURL().toString())
                        .setAttribute("http.status", HttpStatus.valueOf(response.getStatus()).name().toUpperCase())
                        .setAttribute("http.statusCode", response.getStatus())
                        .setAttribute("http.requestURI", request.getRequestURI())
                        .setAttribute("http.queryString", request.getQueryString())
                        .setAttribute("http.serverName", request.getServerName())
                        .setAttribute("http.serverPort", request.getServerPort())
                ).start();
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {
        Instrumentor.end(spanScope, throwable);
    }

    public static String generateSpanName(final HttpServletRequest request) {

        return request.getMethod() + " " + request.getRequestURI();
    }
}
