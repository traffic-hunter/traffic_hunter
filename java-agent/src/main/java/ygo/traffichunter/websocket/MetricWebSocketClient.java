package ygo.traffichunter.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.net.URI;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.retry.backoff.BackOffPolicy;

@Deprecated(since = "1.0", forRemoval = true)
public class MetricWebSocketClient extends WebSocketClient {

    private static final Logger log = Logger.getLogger(MetricWebSocketClient.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MetricWebSocketClient(final URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        final String httpStatusMessage = serverHandshake.getHttpStatusMessage();

        log.info("websocket client opened " + httpStatusMessage);
    }

    @Override
    public void onMessage(final String s) {
        log.info("websocket client received: " + s);
    }

    @Override
    public void onClose(final int i, final String s, final boolean b) {
        log.info("websocket client closed");
    }

    @Override
    public void onError(final Exception e) {
        log.severe("websocket client error: " + e.getMessage());
    }

    public void retry(final int maxAttempt, final BackOffPolicy backOffPolicy) {

        final RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxAttempt)
                .retryOnException(e -> e instanceof IllegalStateException)
                .failAfterMaxAttempts(true)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                        backOffPolicy.getIntervalMillis(),
                        backOffPolicy.getMultiplier())
                )
                .build();

        final Retry retry = Retry.of("websocket", retryConfig);

        final Supplier<MetricWebSocketClient> websocketClientIsNotOpen = retryWebSocketConnection(retry);

        if(websocketClientIsNotOpen.get().isOpen()) {
            log.info("connected to websocket client");
        } else {
            log.info("disconnected from websocket client");
        }
    }

    private Supplier<MetricWebSocketClient> retryWebSocketConnection(final Retry retry) {

        return Retry.decorateSupplier(retry, () -> {
            log.info("Attempting to connect to websocket");
            if (!this.isOpen()) {
                this.connect();
                if (!this.isOpen()) {
                    throw new IllegalStateException("Websocket client is not open");
                }
            }
            return this;
        });
    }

    public void toSend(final SystemInfo systemInfo) {
        try {
            final String s = objectMapper.writeValueAsString(systemInfo);
            this.send(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
