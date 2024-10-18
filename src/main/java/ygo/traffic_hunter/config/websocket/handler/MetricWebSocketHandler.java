package ygo.traffic_hunter.config.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ygo.traffic_hunter.presentation.response.systeminfo.SystemInfo;

@Slf4j
@Component
@Deprecated(since = "1.0", forRemoval = true)
public class MetricWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> webSocketSessions;

    public MetricWebSocketHandler() {
        this.webSocketSessions = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("New connection established = {}", session.getId());
        webSocketSessions.add(session);
        session.sendMessage(new TextMessage("New connection established session id: " + session.getId()));
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        final String payload = message.getPayload();

        final SystemInfo systemInfo = objectMapper.readValue(payload, SystemInfo.class);

        log.info("received metric response = {}", systemInfo);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        log.info("Connection closed = {}", session.getId());
        webSocketSessions.remove(session);
        session.sendMessage(new TextMessage("Connection closed"));
    }

    public int sessionSize() {
        return webSocketSessions.size();
    }
}
