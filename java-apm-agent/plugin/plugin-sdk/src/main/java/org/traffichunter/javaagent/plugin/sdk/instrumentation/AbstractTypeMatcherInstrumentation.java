package org.traffichunter.javaagent.plugin.sdk.instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;

public abstract class AbstractTypeMatcherInstrumentation {

    private final String pluginName;

    private final String pluginModuleVersion;

    public AbstractTypeMatcherInstrumentation(final String pluginName, final String pluginModuleVersion) {
        this.pluginName = pluginName;
        this.pluginModuleVersion = pluginModuleVersion;
    }

    protected ElementMatcher.Junction<ClassLoader> classLoaderMatcher() { return any(); }

    public abstract ElementMatcher<TypeDescription> typeMatcher();

    public Junction<TypeDescription> ignorePackage() {
        return null;
    }

    protected abstract ElementMatcher<? super MethodDescription> isMethod();
}
