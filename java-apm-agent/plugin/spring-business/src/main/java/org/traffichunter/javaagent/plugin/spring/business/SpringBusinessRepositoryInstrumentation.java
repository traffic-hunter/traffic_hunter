package org.traffichunter.javaagent.plugin.spring.business;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.traffichunter.javaagent.plugin.instrumentation.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;
import org.traffichunter.javaagent.plugin.spring.business.helper.SpringBusinessInstrumentationHelper;

public class SpringBusinessRepositoryInstrumentation extends AbstractPluginInstrumentation {

    public SpringBusinessRepositoryInstrumentation() {
        super("spring-business", SpringBusinessRepositoryInstrumentation.class.getName(), "3.3");
    }

    @Override
    public List<Advice> transform() {
        return Collections.singletonList(
                Advice.create(
                isMethod(),
                SpringBusinessRepositoryInstrumentation.class.getName() + "$RepositoryAdvice"
        ));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return isAnnotatedWith(named("org.springframework.stereotype.Repository"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return ElementMatchers.isMethod();
    }

    @SuppressWarnings("unused")
    public static class RepositoryAdvice {

        @OnMethodEnter
        public static SpanScope enter(@Origin final Method method) {

            Context parentContext = Context.current();

            return SpringBusinessInstrumentationHelper.Repository.start(method, parentContext);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void exit(final SpanScope spanScope, final Throwable throwable) {

            SpringBusinessInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
