package ygo.traffichunter.agent.engine.sender.websocket;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.util.AgentUtil;

class AgentTransactionMetricSenderTest extends AbstractTest {

    @Test
    void 웹소켓_송신을_확인한다() throws InterruptedException {
        // given
        WebSocketClient client = new WebSocketClient(URI.create(AgentUtil.WEBSOCKET_URL.getUrl("localhost:9100"))){
            @Override
            public void onOpen(final ServerHandshake handshakedata) {
                System.out.println(handshakedata.getHttpStatusMessage());
                System.out.println("connection opened");
            }

            @Override
            public void onMessage(final String message) {
                System.out.println("received message: " + message);
            }

            @Override
            public void onClose(final int code, final String reason, final boolean remote) {
                System.out.println(code + " " + reason + " " + remote);
                System.out.println("connection closed");
            }

            @Override
            public void onError(final Exception ex) {
                System.out.println("error = " + ex.getMessage());
            }
        };

        // when
        client.connectBlocking(3, TimeUnit.SECONDS);

        Assertions.assertTrue(client.isOpen());

        client.send("hello");
        // then
        client.close();
    }
}