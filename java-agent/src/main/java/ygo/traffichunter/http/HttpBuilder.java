package ygo.traffichunter.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

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
