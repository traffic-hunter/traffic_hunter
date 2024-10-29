package ygo.traffichunter.agent.engine.sender.http;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.engine.AgentExecutionEngine.MetricCollectSupport;
import ygo.traffichunter.agent.engine.sender.TrafficHunterAgentSender;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.http.HttpBuilder;
import ygo.traffichunter.retry.RetryHelper;

public class AgentSystemMetricSender implements TrafficHunterAgentSender<SystemInfo, Supplier<HttpResponse<String>>>, Runnable {

    private static final Logger log = LoggerFactory.getLogger(AgentSystemMetricSender.class);

    private final TrafficHunterAgentProperty property;

    public AgentSystemMetricSender(TrafficHunterAgentProperty property) {
        this.property = property;
    }

    @Override
    public void run() {
        final SystemInfo systemInfo = MetricCollectSupport.collect(property.targetJVMPath());

        final HttpResponse<String> httpResponse = RetryHelper.start(property.backOffPolicy(), property.maxAttempt())
                .failAfterMaxAttempts(true)
                .retryName("httpResponse")
                .throwable(throwable -> throwable instanceof RuntimeException)
                .retrySupplier(this.toSend(systemInfo));

        log.info("httpResponse status = {}", httpResponse.statusCode());
    }

    @Override
    public Supplier<HttpResponse<String>> toSend(final SystemInfo systemInfo) {
        return () -> {
            try {
                return HttpBuilder.newBuilder(property.uri())
                        .header("Content-Type", "application/json")
                        .timeOut(Duration.ofSeconds(3))
                        .request(systemInfo)
                        .build();
            } catch (Exception e) {
                log.error("http request error = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
}
