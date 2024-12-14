/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
package ygo.traffichunter.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Deprecated(since = "1.0.0")
public class HttpBuilder {

    private final URI uri;

    private final ObjectMapper objectMapper;

    private Object obj;

    private String headerKey;

    private String headerValue;

    private Duration timeout = Duration.ofSeconds(10);

    private HttpBuilder(final URI uri) {
        this.uri = uri;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static HttpBuilder newBuilder(final URI uri) {
        return new HttpBuilder(uri);
    }

    public HttpBuilder header(final String key, final String value) {
        this.headerKey = key;
        this.headerValue = value;

        return this;
    }

    public HttpBuilder timeOut(final Duration timeout) {
        this.timeout = timeout;

        return this;
    }

    public HttpBuilder request(final Object obj) {
        this.obj = obj;

        return this;
    }

    public HttpResponse<String> build() throws Exception {
        try (final HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .connectTimeout(timeout)
                .build()) {

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .version(Version.HTTP_1_1)
                    .header(headerKey, headerValue)
                    .timeout(timeout)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(obj)))
                    .build();

            return client.send(request, BodyHandlers.ofString());
        }
    }
}
