package org.traffichunter.javaagent.plugin.spring.business;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

public class SpringBusinessRepositoryInstrumentation extends AbstractPluginInstrumentation {

    public SpringBusinessRepositoryInstrumentation() {
        super("spring-business", SpringBusinessRepositoryInstrumentation.class.getName(), "3.3");
    }

    @Override
    public void transform(final Transformer transformer) {

        transformer.processAdvice(
                Advices.create(
                        isMethod(),
                        RepositoryAdvice.class
                )
        );
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

        @OnMethodEnter(suppress = Throwable.class)
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
