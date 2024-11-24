package ygo.traffic_hunter.core.websocket.handler;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import ygo.traffic_hunter.core.channel.MetricChannelFactory;
import ygo.traffic_hunter.core.channel.MetricProcessingChannel;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<WebSocketSession, MetricProcessingChannel> channelMap = new ConcurrentHashMap<>();

    private final MetricChannelFactory factory;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        log.info("New connection established = {}", session.getId());
        channelMap.put(session, factory.createChannel());
        session.sendMessage(new TextMessage("New connection established session id: " + session.getId()));
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
        log.info("Connection closed = {}", session.getId());
        MetricProcessingChannel remove = channelMap.remove(session);
        remove.close();
        session.close();
        session.sendMessage(new TextMessage("Connection closed"));
    }
}
