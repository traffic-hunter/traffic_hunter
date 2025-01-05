package org.traffichunter.javaagent.plugin.sdk.loader;

import java.util.List;

public interface PluginLoader<P> {

    List<P> loadModules(ClassLoader classLoader);
}
