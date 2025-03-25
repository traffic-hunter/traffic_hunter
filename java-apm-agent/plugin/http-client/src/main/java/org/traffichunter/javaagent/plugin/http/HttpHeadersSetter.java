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

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.net.http.HttpHeaders;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class HttpHeadersSetter implements TextMapSetter<HttpHeaders> {

    public static HttpHeadersSetter SETTER = new HttpHeadersSetter(GlobalOpenTelemetry.getPropagators());

    private final ContextPropagators contextPropagators;

    public HttpHeadersSetter(final ContextPropagators contextPropagators) {
        this.contextPropagators = contextPropagators;
    }

    @Override
    public void set(final HttpHeaders httpHeaders, final String k, final String v) {}

    public HttpHeaders inject(final HttpHeaders httpHeaders) {

        Map<String, List<String>> headerMap = new HashMap<>(httpHeaders.map());

        contextPropagators.getTextMapPropagator()
                .inject(
                        Context.current(),
                        headerMap,
                        (carrier, k, v) -> carrier.put(k, Collections.singletonList(v))
                );

        return HttpHeaders.of(headerMap, (s1, s2) -> true);
    }
}
