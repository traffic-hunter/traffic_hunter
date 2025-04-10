/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.extension;

import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Extendable;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating;
import org.traffichunter.javaagent.bootstrap.BootstrapLogger;
import org.traffichunter.javaagent.bootstrap.Configurations;
import org.traffichunter.javaagent.bootstrap.Configurations.ConfigProperty;
import org.traffichunter.javaagent.commons.status.AgentStatus;
import org.traffichunter.javaagent.commons.util.UUIDGenerator;
import org.traffichunter.javaagent.extension.bytebuddy.AdjustTransformer;
import org.traffichunter.javaagent.extension.env.ConfigurableEnvironment;
import org.traffichunter.javaagent.extension.env.Environment;
import org.traffichunter.javaagent.extension.property.TrafficHunterAgentProperty;
import org.traffichunter.javaagent.extension.bytebuddy.AgentIgnoreMatcher;
import org.traffichunter.javaagent.extension.bytebuddy.AgentLocationStrategy;
import org.traffichunter.javaagent.extension.bytebuddy.ByteBuddyLogger.RedefinitionStrategyLoggingAdapter;
import org.traffichunter.javaagent.extension.bytebuddy.ByteBuddyLogger.TransformLoggingListenAdapter;
import org.traffichunter.javaagent.extension.loader.PluginLoader;
import org.traffichunter.javaagent.extension.loader.TrafficHunterPluginLoader;
import org.traffichunter.javaagent.extension.metadata.AgentMetadata;

/**
 * The {@code ConfigurableContextInitializer} class is responsible for initializing the environment,
 * setting up agent metadata, and configuring ByteBuddy for runtime class transformations.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads properties using a configurable environment.</li>
 *     <li>Sets up agent metadata based on the current environment and runtime status.</li>
 *     <li>Configures ByteBuddy to instrument methods annotated with Spring component annotations.</li>
 * </ul>
 *
 * @see ConfigurableEnvironment
 * @see TrafficHunterAgentProperty
 * @see ByteBuddy
 * @see AgentMetadata
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public final class ConfigurableContextInitializer {

    private static final BootstrapLogger log = BootstrapLogger.getLogger(ConfigurableContextInitializer.class);

    private static final Boolean transformLogging = Configurations.debug(ConfigProperty.TRANSFORM_DEBUG);

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

    /**
     * Load all plugins to manipulate the target application's bytecode.
     */
    public void retransform(final Instrumentation inst) {

        List<AbstractPluginInstrumentation> plugins = loadPlugins(TrafficHunterAgentStartAction.class.getClassLoader());

        printLoadedPlugins(plugins);

        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .ignore(AgentIgnoreMatcher.ignore())
                .disableClassFormatChanges()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .with(Reiterating.INSTANCE)
                .with(new RedefinitionStrategyLoggingAdapter())
                .with(new AgentLocationStrategy());

        if(transformLogging) {
            agentBuilder = agentBuilder.with(new TransformLoggingListenAdapter());
        }

        agentBuilder = instrument(agentBuilder, plugins);

        agentBuilder.installOn(inst);
    }

    AgentBuilder instrument(AgentBuilder originalAgentBuilder,
                            final List<AbstractPluginInstrumentation> pluginInstrumentation) {

        if(isEnabled(pluginInstrumentation)) {
            log.warn("Instrumenting is empty!");
            return originalAgentBuilder;
        }

        for(final AbstractPluginInstrumentation plugin : pluginInstrumentation) {

            Extendable transform = originalAgentBuilder
                    .type(plugin.typeMatcher())
                    .transform(new AdjustTransformer());

            Transformer transformer = new Transformer(transform);

            plugin.transform(transformer);

            originalAgentBuilder = transformer.agentBuilder();
        }

        return originalAgentBuilder;
    }

    private boolean isEnabled(final List<AbstractPluginInstrumentation> pluginInstrumentation) {
        return pluginInstrumentation == null || pluginInstrumentation.isEmpty();
    }

    public AgentMetadata setAgentMetadata(final Instant startTime, final AgentStatus status) {

        final String agentName = property().name();

        return new AgentMetadata(
                UUIDGenerator.generate(agentName),
                Environment.VERSION.version(),
                agentName,
                startTime,
                new AtomicReference<>(status)
        );
    }

    private List<AbstractPluginInstrumentation> loadPlugins(final ClassLoader classLoader) {

        PluginLoader<AbstractPluginInstrumentation> pluginLoader = new TrafficHunterPluginLoader();

        return pluginLoader.loadModules(classLoader);
    }

    private void printLoadedPlugins(final List<AbstractPluginInstrumentation> plugins) {

        plugins.forEach(pluginInstrumentation ->
                log.info("loaded : {}", pluginInstrumentation.getPluginDetailName())
        );

        log.info("Total plugins : {}", plugins.size());
    }
}
