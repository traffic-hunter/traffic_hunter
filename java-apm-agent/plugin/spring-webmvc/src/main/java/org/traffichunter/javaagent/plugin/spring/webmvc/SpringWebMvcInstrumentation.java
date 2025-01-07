package org.traffichunter.javaagent.plugin.spring.webmvc;

import static net.bytebuddy.matcher.ElementMatchers.named;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.AbstractTypeMatcherInstrumentation;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.PluginInstrumentation;
import org.traffichunter.javaagent.plugin.spring.webmvc.helper.SpringWebMvcInstrumentationHelper;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

public class SpringWebMvcInstrumentation extends AbstractTypeMatcherInstrumentation implements PluginInstrumentation {

    public SpringWebMvcInstrumentation() {
        super("spring-webmvc", SpringWebMvcInstrumentation.class.getSimpleName(),"spring-webmvc-6.2.0");
    }

    @Override
    public Transformer transform() {
        return ((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.method(this.isMethod()).intercept(Advice.to(SpringWebMvcDispatcherServletAdvice.class)));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("org.springframework.web.servlet.DispatcherServlet");
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return named("doDispatch");
    }

    @SuppressWarnings("unused")
    public static class SpringWebMvcDispatcherServletAdvice {

        @OnMethodEnter
        public static SpanScope enter(@Origin final Method method,
                                      @Argument(0) final HttpServletRequest request,
                                      @Argument(1) final HttpServletResponse response) {

            return SpringWebMvcInstrumentationHelper.start(method, request);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void end(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {
            SpringWebMvcInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
