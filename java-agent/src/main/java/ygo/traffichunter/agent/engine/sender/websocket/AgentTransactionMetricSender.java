package ygo.traffichunter.agent.engine.sender.websocket;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.sender.TrafficHunterAgentSender;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements TrafficHunterAgentSender<List<TransactionInfo>, Supplier<Void>>, Runnable {

    private final TrafficHunterAgentProperty property;

    private final MetricWebSocketClient<TransactionInfo> client;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property) {
        this.property = property;
        this.client = new MetricWebSocketClient<>(property.uri());
    }

    @Override
    public void run() {

        final List<TransactionInfo> txInfoList = MetricCollectSupport.collect();

        RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("websocket")
                .throwable(throwable -> throwable instanceof RuntimeException)
                .retrySupplier(this.toSend(txInfoList));

    }

    @Override
    public Supplier<Void> toSend(final List<TransactionInfo> input) {
        return () -> {
            try {
                if (client.isConnected(3, TimeUnit.SECONDS)) {
                    client.toSend(input);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }

            return null;
        };
    }
}
