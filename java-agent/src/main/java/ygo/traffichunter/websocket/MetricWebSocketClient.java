package ygo.traffichunter.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricWebSocketClient<M> extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(MetricWebSocketClient.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicBoolean isClosed =  new AtomicBoolean(false);

    public MetricWebSocketClient(final URI serverUri) {
        super(serverUri);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        log.info("websocket client opened");
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

    public boolean isConnected() {
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

    /**
     * recursive close thread safe CAS
     */
    public void close() {
        if(isClosed.compareAndSet(false, true)) {
            try {
                if(isOpen()) {
                    this.closeBlocking();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void toSend(final M metric) {
        if(!isOpen()) {
            throw new IllegalStateException("WebSocket client is closed");
        }

        try {
            String s = objectMapper.writeValueAsString(metric);
            this.send(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void toSend(final List<M> metrics) {
        if(!isOpen()) {
            throw new IllegalStateException("WebSocket client is closed");
        }

        try {
            this.send(objectMapper.writeValueAsString(metrics));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
