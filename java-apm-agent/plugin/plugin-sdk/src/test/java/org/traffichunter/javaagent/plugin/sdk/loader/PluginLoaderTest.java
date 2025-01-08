package org.traffichunter.javaagent.plugin.sdk.loader;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.AbstractPluginInstrumentation;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PluginLoaderTest {

    @Test
    void ServiceLoader는_구현체들을_불러온다() {
        // given
        PluginLoader<AbstractPluginInstrumentation> loader = new TrafficHunterPluginLoader();

        // when
        List<AbstractPluginInstrumentation> plugins = loader.loadModules(PluginLoaderTest.class.getClassLoader());

        // then
        System.out.println(plugins.size());
    }

    @Test
    void ClassLoader를_확인한다() {

    }
}