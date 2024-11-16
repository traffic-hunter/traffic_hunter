package ygo.traffic_hunter.presentation.websocket;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> webSocketSessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("New connection established = {}", session.getId());
        webSocketSessions.add(session);
        session.sendMessage(new TextMessage("New connection established session id: " + session.getId()));
    }

    @Override
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {
        ByteBuffer byteBuffer = message.getPayload();

        byte[] data = new byte[byteBuffer.remaining()];


    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, @NotNull final CloseStatus status) throws Exception {
        log.info("Connection closed = {}", session.getId());
        webSocketSessions.remove(session);
        session.sendMessage(new TextMessage("Connection closed"));
    }

    public int sessionSize() {
        return webSocketSessions.size();
    }
}
