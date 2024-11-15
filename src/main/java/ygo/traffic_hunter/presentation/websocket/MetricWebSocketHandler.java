package ygo.traffic_hunter.presentation.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ygo.traffic_hunter.core.TrafficHunterService;
import ygo.traffic_hunter.presentation.response.systeminfo.TransactionInfo;
import ygo.traffic_hunter.presentation.response.systeminfo.metadata.MetadataWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> webSocketSessions = ConcurrentHashMap.newKeySet();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TypeReference<MetadataWrapper<TransactionInfo>> typeReference = new TypeReference<>() {};

    private final TrafficHunterService trafficHunterService;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("New connection established = {}", session.getId());
        webSocketSessions.add(session);
        session.sendMessage(new TextMessage("New connection established session id: " + session.getId()));
    }

    @Override
    protected void handleTextMessage(@NotNull final WebSocketSession session, final TextMessage message) throws Exception {

        final String payload = message.getPayload();

        MetadataWrapper<TransactionInfo> metadataWrapper = objectMapper.readValue(payload, typeReference);

        log.info("received transaction response = {}", metadataWrapper);
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
