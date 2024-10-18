package ygo.traffic_hunter.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ygo.traffic_hunter.config.websocket.handler.MetricWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Deprecated(since = "1.0", forRemoval = true)
public class WebSocketConfig implements WebSocketConfigurer {

    private final MetricWebSocketHandler metricWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(metricWebSocketHandler, "/traffic-hunter")
                .setAllowedOrigins("*");
    }
}
