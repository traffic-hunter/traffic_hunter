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

import static org.traffichunter.javaagent.plugin.sdk.support.http.HttpInstrumentationSupport.HttpHeaderExtractor;

import io.opentelemetry.context.Context;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HttpClientInstrumentationHelper {

    public static SpanScope start(final HttpRequest request, final Context currentContext) {

        HttpHeaderExtractor headerExtractor = new HttpHeaderExtractor(request.headers());

        headerExtractor.mapToHeaderName("x-forwarded-for")
                .mapToHeaderName("Accept")
                .mapToHeaderName("Referer")
                .mapToHeaderName("Origin")
                .mapToHeaderName("Host");

        return Instrumentor.startBuilder(request)
                .context(currentContext)
                .spanAttribute((span, req) ->
                        span.setAttribute("uri", req.uri().toString())
                            .setAttribute("port", port(req))
                            .setAttribute("host", host(req))
                            .setAttribute("req_headers", headerExtractor.getHeaderMap().toString())
                ).start();
    }

    public static void end(final SpanScope spanScope,
                           final HttpResponse<?> response,
                           final Throwable throwable) {

        HttpHeaderExtractor headerExtractor = new HttpHeaderExtractor(response.headers());

        Instrumentor.endBuilder(response)
                .spanScope(spanScope)
                .throwable(throwable)
                .spanAttribute((span, res) ->
                        span.setAttribute("status_code", res.statusCode())
                            .setAttribute("version", version(res))
                            .setAttribute("res_headers", headerExtractor.getHeaderMap().toString())
                ).end();
    }

    private static String version(final HttpResponse<?> response) {
        if(response != null && response.version() == Version.HTTP_2) {
            return Version.HTTP_2.name();
        }

        return Version.HTTP_1_1.name();
    }

    private static String host(final HttpRequest request) {
        return request.uri().getHost();
    }

    private static String port(final HttpRequest request) {
        String scheme = request.uri().getScheme();

        if(scheme == null) {
            return String.valueOf(HttpSpec.HTTP.getPort());
        }

        if(scheme.equals(HttpSpec.HTTP.getProtocol())) {
            return String.valueOf(HttpSpec.HTTP.getPort());
        }

        if(scheme.equals(HttpSpec.HTTPS.getProtocol())) {
            return String.valueOf(HttpSpec.HTTPS.getPort());
        }

        return "";
    }
}
