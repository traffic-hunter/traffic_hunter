package ygo.traffichunter.agent.engine.env.yaml;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ygo.traffichunter.agent.TrafficHunterAgent;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.yaml.bind.RelaxedBindingUtils;
import ygo.traffichunter.agent.engine.env.yaml.root.RootYamlProperty;
import ygo.traffichunter.agent.engine.env.yaml.root.agent.retry.backoff.BackOffSubProperty;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.retry.backoff.BackOffPolicy;
import ygo.traffichunter.retry.backoff.policy.ExponentialBackOffPolicy;
import ygo.traffichunter.util.FileUtils;

/**
 * The {@code YamlConfigurableEnvironment} class implements {@link ConfigurableEnvironment}
 * to load configuration properties from a YAML file or input stream.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads configuration properties from a YAML file or input stream.</li>
 *     <li>Parses YAML data into a {@link TrafficHunterAgentProperty} instance using
 *         custom relaxed binding rules.</li>
 *     <li>Supports a default configuration file or custom file paths.</li>
 * </ul>
 *
 * @see ConfigurableEnvironment
 * @see TrafficHunterAgentProperty
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class YamlConfigurableEnvironment implements ConfigurableEnvironment {

    private static final String DEFAULT_CONFIG_FILE = "agent-env.yml";

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

    static class YamlParser {

        private static TrafficHunterAgentProperty parse(final RootYamlProperty root){

            return TrafficHunterAgent.connect(root.getAgent().getServerUri())
                    .name(root.getAgent().getName())
                    .targetUri(root.getAgent().getTargetUri())
                    .locationJar(root.getAgent().getJar())
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
