package ygo.traffichunter.agent.engine.sender.websocket;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.MetadataWrapper;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.websocket.MetricWebSocketClient;

public class AgentTransactionMetricSender implements MetricSender<Supplier<MetadataWrapper<List<TransactionInfo>>>>, Runnable {

    private static final Logger log = LoggerFactory.getLogger(AgentTransactionMetricSender.class);

    private final TrafficHunterAgentProperty property;

    private final MetricWebSocketClient<MetadataWrapper<List<TransactionInfo>>> client;

    private final AgentExecutableContext context;

    public AgentTransactionMetricSender(final TrafficHunterAgentProperty property,
                                        final AgentExecutableContext context) {

        this.context = context;
        this.property = property;
        this.client = new MetricWebSocketClient<>(property.uri());
    }

    @Override
    public void run()  {

        final MetadataWrapper<List<TransactionInfo>> response = RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("websocket")
                .throwable(throwable -> throwable instanceof RuntimeException)
                .retrySupplier(this.toSend());

        log.info("Websocket response: {}", response);
    }

    @Override
    public Supplier<MetadataWrapper<List<TransactionInfo>>> toSend() throws RuntimeException {
        return () -> {

            final List<TransactionInfo> txInfoList = MetricCollectSupport.collect();

            final MetadataWrapper<List<TransactionInfo>> metadataWrapper = new MetadataWrapper<>(
                    context.configureEnv().getAgentMetadata(),
                    txInfoList
            );

            if (client.isConnected(3, TimeUnit.SECONDS)) {
                client.toSend(metadataWrapper);
            }

            return metadataWrapper;
        };
    }
}
