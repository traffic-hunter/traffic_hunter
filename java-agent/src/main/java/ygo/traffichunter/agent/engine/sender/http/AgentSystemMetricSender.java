package ygo.traffichunter.agent.engine.sender.http;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.collect.MetricCollectSupport;
import ygo.traffichunter.agent.engine.context.AgentExecutableContext;
import ygo.traffichunter.agent.engine.sender.MetricSender;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.engine.systeminfo.metadata.MetadataWrapper;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.http.HttpBuilder;
import ygo.traffichunter.retry.RetryHelper;
import ygo.traffichunter.util.AgentUtil;

public class AgentSystemMetricSender implements MetricSender<Supplier<HttpResponse<String>>> {

    private static final Logger log = LoggerFactory.getLogger(AgentSystemMetricSender.class);

    private final TrafficHunterAgentProperty property;

    private final AgentMetadata metadata;

    public AgentSystemMetricSender(final TrafficHunterAgentProperty property,
                                   final AgentMetadata metadata) {
        this.property = property;
        this.metadata = metadata;
    }

    public void run() {

        final HttpResponse<String> httpResponse = RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("httpResponse")
                .throwable(throwable -> throwable instanceof RuntimeException)
                .retrySupplier(this.toSend());

        log.info("httpResponse status = {}", httpResponse.statusCode());
    }

    @Override
    public Supplier<HttpResponse<String>> toSend() {
        return () -> {

            final SystemInfo systemInfo = MetricCollectSupport.collect(property.targetJVMPath());

            final MetadataWrapper<SystemInfo> metadataWrapper = new MetadataWrapper<>(
                    metadata,
                    systemInfo
            );

            try {
                return HttpBuilder.newBuilder(URI.create(AgentUtil.HTTP_URL.getUrl(property.uri())))
                        .header("Content-Type", "application/json")
                        .timeOut(Duration.ofSeconds(3))
                        .request(metadataWrapper)
                        .build();
            } catch (Exception e) {
                log.error("http request error = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
}
