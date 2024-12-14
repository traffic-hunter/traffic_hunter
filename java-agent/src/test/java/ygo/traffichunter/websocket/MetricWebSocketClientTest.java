package ygo.traffichunter.websocket;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.metric.transaction.TransactionInfo;
import ygo.traffichunter.util.AgentUtil;

class MetricWebSocketClientTest extends AbstractTest {

    @Test
    void 웹소켓_통신_할_때_서버가_끊긴경우_예외_발생() {

        MetricWebSocketClient client = new MetricWebSocketClient(URI.create(AgentUtil.WEBSOCKET_URL.getUrl("localhost:9100")),
                new AgentMetadata("test", "Test", "test", Instant.now(), new AtomicReference<>(AgentStatus.INITIALIZED)));

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