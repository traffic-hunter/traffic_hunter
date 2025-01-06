package org.traffichunter.javaagent.plugin.sdk.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

public interface PluginInstrumentation {

    AgentBuilder.Transformer transform();
}
