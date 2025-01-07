package org.traffichunter.javaagent.plugin.jdbc;

import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
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

public class ConnectionPluginInstrumentation extends AbstractTypeMatcherInstrumentation implements PluginInstrumentation {

    public ConnectionPluginInstrumentation() {
        super("jdbc", ConnectionPluginInstrumentation.class.getSimpleName(), "");
    }

    @Override
    public Transformer transform() {
        return (builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.method(this.isMethod()).intercept(Advice.to(ConnectionAdvice.class));
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("java.sql.Connection");
    }

    @Override
    protected ElementMatcher<? super MethodDescription> isMethod() {
        return named("prepareStatement");
    }

    @SuppressWarnings("unused")
    public static class ConnectionAdvice {

        @OnMethodEnter
        public static SpanScope start(@Argument(0) final String sql) {
            Context parentContext = Context.current();

            return JdbcInstrumentationHelper.ConnectionInstrumentation.start(sql, parentContext);
        }

        @OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {
            JdbcInstrumentationHelper.end(spanScope, throwable);
        }
    }
}
