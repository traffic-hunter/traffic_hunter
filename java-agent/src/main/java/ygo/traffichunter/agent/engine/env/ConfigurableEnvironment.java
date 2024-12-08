package ygo.traffichunter.agent.engine.env;

import java.io.InputStream;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

/**
 * The {@code ConfigurableEnvironment} interface defines the contract for loading
 * agent configuration properties. It supports loading configuration from
 * default or provided input sources.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads configuration properties into a {@link TrafficHunterAgentProperty} instance.</li>
 *     <li>Supports default and custom input streams for configuration sources.</li>
 * </ul>
 *
 * @see TrafficHunterAgentProperty
 * @see YamlConfigurableEnvironment
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public interface ConfigurableEnvironment {

    TrafficHunterAgentProperty load();

    TrafficHunterAgentProperty load(InputStream is);
}
