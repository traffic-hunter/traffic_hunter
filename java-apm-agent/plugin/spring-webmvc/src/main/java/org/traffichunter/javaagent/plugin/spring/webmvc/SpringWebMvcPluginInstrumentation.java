package org.traffichunter.javaagent.plugin.spring.webmvc;

import static net.bytebuddy.matcher.ElementMatchers.named;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.PluginInstrumentation;

public class SpringWebMvcPluginInstrumentation implements PluginInstrumentation {

    @Override
    public void transform(final Transformer transformer, final ClassLoader classLoader) {

    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("org.springframework.web.servlet.DispatcherServlet");
    }

    @Override
    public Junction<TypeDescription> ignorePackage() {
        return null;
    }

    public static class SpringWebMvcDispatcherServletAdvice {

        @OnMethodEnter
        public static void enter() {

        }
    }
}
