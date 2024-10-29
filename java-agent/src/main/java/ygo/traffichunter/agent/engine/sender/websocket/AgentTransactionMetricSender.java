package ygo.traffichunter.agent.engine.sender.websocket;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import ygo.traffichunter.agent.engine.instrument.collect.TransactionMetric;
import ygo.traffichunter.agent.engine.sender.TrafficHunterAgentSender;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements TrafficHunterAgentSender<TransactionMetric, Void> {

    private final TrafficHunterAgentProperty property;

    private final MetricWebSocketClient<TransactionMetric> client;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property,
                                        final MetricWebSocketClient<TransactionMetric> client) {
        this.property = property;
        this.client = client;
    }

    @Override
    public Void toSend(final TransactionMetric input) {
        RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("websocket")
                .throwable(throwable -> throwable instanceof RuntimeException)
                .retrySupplier(() -> {
                    try {
                        if(client.isConnected(3, TimeUnit.SECONDS)) {
                            client.toSend(input);
                        }
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }

                    return null;
                });

        return null;
    }
}
