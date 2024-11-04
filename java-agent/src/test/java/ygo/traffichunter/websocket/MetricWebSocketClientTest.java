package ygo.traffichunter.websocket;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ygo.TestExt;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.util.AgentUtil;

class MetricWebSocketClientTest extends TestExt {

    @Test
    void 웹소켓_통신_할_때_서버가_끊긴경우_예외_발생() {

        MetricWebSocketClient<TransactionInfo> client =
                new MetricWebSocketClient<>(URI.create(AgentUtil.WEBSOCKET_URL.getUrl("localhost:9100")));

        TransactionInfo txInfo = TransactionInfo.create(
                "test",
                Instant.now(),
                Instant.now(),
                153,
                "test",
                true
                );

        Assertions.assertThrows(RuntimeException.class, () -> client.toSend(txInfo));
    }
}