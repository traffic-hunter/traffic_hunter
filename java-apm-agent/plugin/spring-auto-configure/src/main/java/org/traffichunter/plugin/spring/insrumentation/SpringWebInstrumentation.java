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
package org.traffichunter.plugin.spring.insrumentation;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class SpringWebInstrumentation {

    public static ClientHttpRequestInterceptor instance() {
        return new ThunterClientHttpRequestInterceptor();
    }

    private static final class ThunterClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(final HttpRequest request,
                                            final byte[] body,
                                            final ClientHttpRequestExecution execution) throws IOException {

            Context parentContext = Context.current();

            SpanScope spanScope = Instrumentor.startBuilder(request)
                    .instrumentationName("spring-rest-client-inst")
                    .context(parentContext)
                    .spanAttribute((span, httpRequest) -> {
                        span.setAttribute("http.method", request.getMethod().toString());
                        span.setAttribute("http.uri", request.getURI().toString());
                    })
                    .start();

            ClientHttpResponse response = null;

            try (Scope ignore = spanScope.scope()) {
                response = execution.execute(request, body);
            } catch (Throwable t) {
                instEnd(response, spanScope, t);
                throw t;
            }

            instEnd(response, spanScope, null);

            return response;
        }

        private void instEnd(final ClientHttpResponse response, final SpanScope spanScope, @Nullable Throwable throwable) {

            Instrumentor.endBuilder(response)
                    .throwable(throwable)
                    .spanScope(spanScope)
                    .spanAttribute((span, response1) -> {

                        try {
                            String statusText = response1.getStatusText();
                            span.setAttribute("http.statusCode", statusText);
                        } catch (IOException ignore) {}
                    })
                    .end();
        }
    }
}
