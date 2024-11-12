package ygo.traffichunter.agent.engine.context.configuration;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.sender.manager.MetricSendSessionManager;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class ConfigurableContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableContextInitializer.class);

    private final ConfigurableEnvironment env;

    public ConfigurableContextInitializer(final ConfigurableEnvironment env) {
        this.env = env;
    }

    public TrafficHunterAgentProperty property() {
        return env.load();
    }

    public TrafficHunterAgentProperty property(final InputStream is) {
        return env.load(is);
    }

    public AgentMetadata getAgentMetadata() {
        return env.getAgentMetadata();
    }

    public AgentMetadata getAgentMetadata(final InputStream is) {
        return env.getAgentMetadata(is);
    }

    public void attach(final TrafficHunterAgentProperty property) {
        try {

            VirtualMachine vm = JVMSelector.getVM(property.targetJVMPath());

            vm.loadAgent(property.jar());
        } catch (IOException | AgentLoadException | AgentInitializationException e) {
            log.error("Failed to load agent = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
