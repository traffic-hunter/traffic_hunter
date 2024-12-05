package ygo.traffic_hunter.core.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import ygo.traffic_hunter.common.map.AgentMapper;
import ygo.traffic_hunter.core.channel.MetricChannelFactory;
import ygo.traffic_hunter.core.channel.MetricProcessingChannel;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.repository.AgentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<WebSocketSession, MetricProcessingChannel> channelMap = new ConcurrentHashMap<>();

    private final Map<String, AgentMetadata> agentMetadataMap = new ConcurrentHashMap<>();

    private final MetricChannelFactory factory;

    private final ObjectMapper objectMapper;

    private final AgentRepository agentRepository;

    private final AgentMapper mapper;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

        log.info("New connection established = {}", session.getId());

        channelMap.put(session, factory.createChannel());

        session.sendMessage(new TextMessage("New connection established session id: " + session.getId()));
    }

    @Override
    @Transactional
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        String payload = message.getPayload();

        log.info("agent info = {}", payload);

        try {

            AgentMetadata agentMetadata = objectMapper.readValue(payload, AgentMetadata.class);

            agentMetadataMap.put(session.getId(), agentMetadata);

            agentRepository.save(mapper.map(agentMetadata));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {
        ByteBuffer byteBuffer = message.getPayload();

        log.info("websocket session id = {}", session.getId());

        MetricProcessingChannel metricProcessingChannel = channelMap.get(session);

        metricProcessingChannel.process(byteBuffer);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {

        log.info("Connection closed = {} {} {}", session.getId(), status.getCode(), status.getReason());

        MetricProcessingChannel remove = channelMap.remove(session);

        agentMetadataMap.remove(session.getId());

        remove.close();

        session.close();
    }

    public List<AgentMetadata> getAgents() {
        return new ArrayList<>(agentMetadataMap.values());
    }
}
