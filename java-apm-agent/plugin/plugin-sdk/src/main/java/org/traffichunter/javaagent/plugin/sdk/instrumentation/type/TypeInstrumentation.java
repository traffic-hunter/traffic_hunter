package org.traffichunter.javaagent.plugin.sdk.instrumentation.type;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;

public interface TypeInstrumentation {

    default ElementMatcher.Junction<ClassLoader> classLoaderMatcher() { return any(); }

    ElementMatcher<TypeDescription> typeMatcher();

    Junction<TypeDescription> ignorePackage();
}
