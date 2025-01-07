package org.traffichunter.javaagent.plugin.jdbc;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.traffichunter.javaagent.plugin.jdbc.helper.JdbcInstrumentationHelper;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.AbstractTypeMatcherInstrumentation;
import org.traffichunter.javaagent.plugin.sdk.instrumentation.PluginInstrumentation;
import org.traffichunter.javaagent.trace.manager.TraceManager.SpanScope;

public class PrepareStatementPluginInstrumentation extends AbstractTypeMatcherInstrumentation
        implements PluginInstrumentation {

    public PrepareStatementPluginInstrumentation() {
        super("jdbc", PrepareStatementPluginInstrumentation.class.getSimpleName(), "");
    }

    @Override
    public Transformer transform() {
        return ((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.method(this.isMethod()).intercept(Advice.to(PrepareStatementAdvice.class)));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("java.sql.PreparedStatement");
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return nameStartsWith("execute");
    }

    @SuppressWarnings("unused")
    public static class PrepareStatementAdvice {

        @OnMethodEnter
        public static SpanScope enter() {
            Context parentContext = Context.current();

            return JdbcInstrumentationHelper.PreparedStatementInstrumentation.start(parentContext);
        }

        @OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {
            JdbcInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
