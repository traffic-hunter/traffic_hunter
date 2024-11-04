package ygo.traffichunter.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricWebSocketClient<M> extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(MetricWebSocketClient.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MetricWebSocketClient(final URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        final String httpStatusMessage = serverHandshake.getHttpStatusMessage();

        log.info("websocket client opened = {}", httpStatusMessage);
    }

    @Override
    public void onMessage(final String s) {
        log.info("websocket client received = {}", s);
    }


    @Override
    public void onClose(final int i, final String s, final boolean b) {
        log.info("websocket client closed");
    }

    @Override
    public void onError(final Exception e) {
        log.error("websocket client error = {}", e.getMessage());
    }

    public boolean isConnected() throws InterruptedException {
        if(isOpen()) {
            return true;
        }

        try {
            return this.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    public boolean isConnected(final long timeOut, final TimeUnit timeUnit) {
        if(isOpen()) {
            return true;
        }

        try {
            return this.connectBlocking(timeOut, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    public void close() {
        try {
            this.closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void toSend(final M metric) throws RuntimeException {
        try {
            if(isOpen()) {
                final String s = objectMapper.writeValueAsString(metric);
                this.send(s);
            } else {
                throw new RuntimeException("WebSocket client is closed");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void toSend(final List<M> metrics) {
        try {
            if(isOpen()) {
                final String s = objectMapper.writeValueAsString(metrics);
                this.send(s);
            } else {
                throw new RuntimeException("WebSocket client is closed");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
