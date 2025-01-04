package org.traffichunter.javaagent.plugin.sdk.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.PluginInstrumentation;

public class TrafficHunterPluginLoader implements PluginLoader<PluginInstrumentation> {

    @Override
    public List<PluginInstrumentation> loadModules(final ClassLoader classLoader) {

        List<PluginInstrumentation> loadPlugIn = new ArrayList<>();

        ServiceLoader<PluginInstrumentation> loader = ServiceLoader.load(PluginInstrumentation.class, classLoader);

        for(PluginInstrumentation plugIn : loader) {
            loadPlugIn.add(plugIn);
        }

        return loadPlugIn;
    }
}
