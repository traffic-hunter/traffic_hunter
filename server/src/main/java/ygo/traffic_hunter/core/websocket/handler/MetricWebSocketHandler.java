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
import ygo.traffic_hunter.core.collector.MetricCollector;
import ygo.traffic_hunter.core.dto.request.metadata.AgentMetadata;
import ygo.traffic_hunter.core.repository.AgentRepository;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricWebSocketHandler extends BinaryWebSocketHandler {

    private final Map<String, AgentMetadata> agentMetadataMap = new ConcurrentHashMap<>();

    private final MetricCollector collector;

    private final ObjectMapper objectMapper;

    private final AgentRepository agentRepository;

    private final AgentMapper mapper;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {

        log.info("New connection established = {}", session.getId());

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

            if(agentRepository.existsByAgentId(agentMetadata.agentId())) {
                return;
            }

            agentRepository.save(mapper.map(agentMetadata));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {
        ByteBuffer byteBuffer = message.getPayload();

        log.info("websocket session id = {}", session.getId());

        collector.collect(byteBuffer);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {

        log.info("Connection closed = {} {} {}", session.getId(), status.getCode(), status.getReason());

        agentMetadataMap.remove(session.getId());

        session.close();
    }

    public List<AgentMetadata> getAgents() {
        return new ArrayList<>(agentMetadataMap.values());
    }
}
