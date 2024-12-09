package ygo.traffic_hunter.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ygo.traffic_hunter.core.websocket.handler.MetricWebSocketHandler;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final MetricWebSocketHandler metricWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(metricWebSocketHandler, "/traffic-hunter/tx")
                .setAllowedOrigins("*");
    }
}
