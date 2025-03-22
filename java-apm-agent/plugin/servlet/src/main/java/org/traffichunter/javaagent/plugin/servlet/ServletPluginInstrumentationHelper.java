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
package org.traffichunter.javaagent.plugin.servlet;

import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class ServletPluginInstrumentationHelper {

    public static SpanScope start(final String methodName,
                                  final Context context,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) {

        return Instrumentor.startBuilder(request)
                .context(context)
                .spanName(req -> req.getMethod() + " " + req.getRequestURI())
                .spanAttribute((span, httpServletRequest) ->
                        span.setAttribute("method.name", methodName)
                        .setAttribute("http.method", httpServletRequest.getMethod())
                        .setAttribute("http.url", httpServletRequest.getRequestURL().toString())
                        .setAttribute("http.status", response.getStatus())
                        .setAttribute("http.statusCode", response.getStatus())
                        .setAttribute("http.requestURI", httpServletRequest.getRequestURI())
                        .setAttribute("http.queryString", httpServletRequest.getQueryString())
                        .setAttribute("http.serverName", httpServletRequest.getServerName())
                        .setAttribute("http.serverPort", httpServletRequest.getServerPort())
                ).start();
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {

        Instrumentor.end(spanScope, throwable);
    }
}
