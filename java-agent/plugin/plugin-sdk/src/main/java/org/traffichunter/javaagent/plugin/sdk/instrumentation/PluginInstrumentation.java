package org.traffichunter.javaagent.plugin.sdk.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.type.TypeInstrumentation;

public interface PluginInstrumentation extends TypeInstrumentation {

    void transform(AgentBuilder.Transformer transformer, ClassLoader classLoader);
}
