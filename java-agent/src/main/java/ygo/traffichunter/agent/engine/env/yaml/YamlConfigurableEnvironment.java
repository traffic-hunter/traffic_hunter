package ygo.traffichunter.agent.engine.env.yaml;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class YamlConfigurableEnvironment implements ConfigurableEnvironment {

    private final Yaml yaml;

    public YamlConfigurableEnvironment() {
        final Constructor constructor = new Constructor(RootYamlProperty.class, new LoaderOptions());

        constructor.setPropertyUtils(new RelaxedBindingUtils());

        this.yaml = new Yaml(constructor);
    }

    @Override
    public TrafficHunterAgentProperty load(final String path) {
        final InputStream is = getClass().getClassLoader().getResourceAsStream(path);

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
