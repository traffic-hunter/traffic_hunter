package ygo.traffichunter.agent.engine.env;

import java.io.InputStream;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public interface ConfigurableEnvironment {

    TrafficHunterAgentProperty load(String path);

    TrafficHunterAgentProperty load(InputStream is);
}
