package org.traffichunter.javaagent.bootstrap.engine.context.configuration;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.plugin.sdk.loader.PluginLoader;
import org.traffichunter.javaagent.plugin.sdk.loader.TrafficHunterPluginLoader;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ConfigurableContextInitializerTest {

    @Test
    void ServiceLoader는_구현체들을_불러온다() {
        // given
        PluginLoader<AbstractPluginInstrumentation> loader = new TrafficHunterPluginLoader();

        // when
        List<AbstractPluginInstrumentation> plugins = loader.loadModules(
                ConfigurableContextInitializer.class.getClassLoader());

        // then
        for (AbstractPluginInstrumentation plugin : plugins) {
            System.out.println(plugin.getClass().getClassLoader());
        }
    }
}