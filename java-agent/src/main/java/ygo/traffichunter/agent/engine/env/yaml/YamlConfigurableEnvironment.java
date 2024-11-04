package ygo.traffichunter.agent.engine.env.yaml;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.TrafficHunterAgent;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.env.yaml.bind.RelaxedBindingUtils;
import ygo.traffichunter.agent.engine.env.yaml.root.RootYamlProperty;
import ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.backoff.BackOffSubProperty;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.backoff.BackOffPolicy;
import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.util.FileUtils;

public class YamlConfigurableEnvironment implements ConfigurableEnvironment {

    private static final String DEFAULT_CONFIG_FILE = "agent-env.yml";

    private final UUID uuid = UUID.randomUUID();

    private final Yaml yaml;

    private final String path;

    public YamlConfigurableEnvironment(final String path) {
        final Constructor constructor = new Constructor(RootYamlProperty.class, new LoaderOptions());

        constructor.setPropertyUtils(new RelaxedBindingUtils());

        this.yaml = new Yaml(constructor);
        this.path = path;
    }

    @Override
    public TrafficHunterAgentProperty load() {
        final InputStream is = FileUtils.getFile(path);

        final RootYamlProperty root = yaml.load(is);

        return YamlParser.parse(root);
    }

    @Override
    public TrafficHunterAgentProperty load(final InputStream is) {

        final RootYamlProperty root = yaml.load(is);

        return YamlParser.parse(root);
    }

    @Override
    public AgentMetadata getAgentMetadata() {
        final InputStream is = FileUtils.getFile(path);

        final RootYamlProperty root = yaml.load(is);

        return new AgentMetadata(
                uuid.toString(),
                Environment.VERSION.version(),
                root.getAgent().getName(),
                Instant.now(),
                AgentStatus.INITIALIZED
        );
    }

    @Override
    public AgentMetadata getAgentMetadata(final InputStream is) {

        final RootYamlProperty root = yaml.load(is);

        return new AgentMetadata(
                uuid.toString(),
                Environment.VERSION.version(),
                root.getAgent().getName(),
                Instant.now(),
                AgentStatus.INITIALIZED
        );
    }

    static class YamlParser {

        private static TrafficHunterAgentProperty parse(final RootYamlProperty root){

            return TrafficHunterAgent.connect(root.getAgent().getServerUri())
                    .targetJVM(root.getAgent().getTarget())
                    .scheduleInterval(root.getAgent().getInterval())
                    .scheduleTimeUnit(TimeUnit.SECONDS)
                    .faultTolerant()
                    .retry(root.getAgent().getRetry().getMaxAttempt())
                    .backOffPolicy(backOffPolicy(root.getAgent().getRetry().getBackoff()))
                    .complete();
        }

        private static BackOffPolicy backOffPolicy(BackOffSubProperty backOffSubProperty) {
            if(backOffSubProperty == null) {
                return ExponentialBackOffPolicy.DEFAULT;
            }

            return new ExponentialBackOffPolicy(
                    backOffSubProperty.getIntervalMillis(),
                    backOffSubProperty.getMultiplier()
            );
        }
    }
}
