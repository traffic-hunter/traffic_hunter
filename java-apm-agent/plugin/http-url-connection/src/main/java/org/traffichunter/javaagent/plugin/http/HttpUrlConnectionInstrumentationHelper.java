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
package org.traffichunter.javaagent.plugin.http;

import io.opentelemetry.context.Context;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
class HttpUrlConnectionInstrumentationHelper {

    public static SpanScope start(final HttpURLConnection httpURLConnection,
                                  final boolean connected,
                                  final Context currentContext) {

        return Instrumentor.startBuilder(httpURLConnection)
                .spanName(urlConnection -> urlConnection.getURL().toString())
                .context(currentContext)
                .spanAttribute((span, urlConnection) ->
                        span.setAttribute("connected", connected)
                                .setAttribute("url", urlConnection.getURL().toExternalForm())
                                .setAttribute("host", urlConnection.getURL().getHost())
                                .setAttribute("port", urlConnection.getURL().getPort())
                                .setAttribute("req_method", urlConnection.getRequestMethod())
                                .setAttribute("using_proxy", urlConnection.usingProxy())
                                .setAttribute("status", getResponseCode(urlConnection))
                ).start();
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {

        Instrumentor.end(spanScope, throwable);
    }

    private static String requestProperty(final HttpURLConnection httpURLConnection,
                                          final String name) {

        String property = httpURLConnection.getRequestProperty(name);

        return property == null ? "" : property;
    }

    private static String responseField(final HttpURLConnection httpURLConnection,
                                        final String name) {

        String headerField = httpURLConnection.getHeaderField(name);

        return headerField == null ? "" : headerField;
    }

    private static int getResponseCode(final HttpURLConnection httpURLConnection) {
        try {
            return httpURLConnection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
