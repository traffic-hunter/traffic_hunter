package org.traffichunter.javaagent.plugin.spring.business;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

public class SpringBusinessServiceInstrumentation extends AbstractPluginInstrumentation {

    public SpringBusinessServiceInstrumentation() {
        super("spring-business", SpringBusinessServiceInstrumentation.class.getName(), "3.3");
    }

    @Override
    public void transform(final Transformer transformer) {

        List<Advice> advice = Collections.singletonList(
                Advice.create(
                        isMethod(),
                        SpringBusinessServiceInstrumentation.class.getName() + "$ServiceAdvice"
                ));

        transformer.processAdvice(advice);
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return isAnnotatedWith(named("org.springframework.stereotype.Service"));
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return ElementMatchers.isMethod();
    }

    @SuppressWarnings("unused")
    public static class ServiceAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static SpanScope enter(@Origin final Method method) {

            Context parentContext = Context.current();

            return SpringBusinessInstrumentationHelper.Service.start(method, parentContext);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {

            SpringBusinessInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
