/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffichunter.agent.engine.context.configuration;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Enter;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.Thrown;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ygo.traffichunter.agent.AgentStatus;
import ygo.traffichunter.agent.engine.env.ConfigurableEnvironment;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.instrument.annotation.AnnotationPath;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
import ygo.traffichunter.trace.opentelemetry.TraceExporter;
import ygo.traffichunter.trace.opentelemetry.TraceManager;
import ygo.traffichunter.trace.opentelemetry.TraceManager.SpanScope;
import ygo.traffichunter.util.UUIDGenerator;

/**
 * The {@code ConfigurableContextInitializer} class is responsible for initializing the environment,
 * setting up agent metadata, and configuring ByteBuddy for runtime class transformations.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Loads properties using a configurable environment.</li>
 *     <li>Sets up agent metadata based on the current environment and runtime status.</li>
 *     <li>Configures ByteBuddy to instrument methods annotated with Spring component annotations.</li>
 * </ul>
 *
 * @see ConfigurableEnvironment
 * @see TrafficHunterAgentProperty
 * @see ByteBuddy
 * @see AgentMetadata
 *
 * @author yungwang-o
 * @version 1.0.0
 */
public class ConfigurableContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableContextInitializer.class);

    private final ConfigurableEnvironment env;

    public ConfigurableContextInitializer(final ConfigurableEnvironment env) {
        this.env = env;
    }

    public TrafficHunterAgentProperty property() {
        return env.load();
    }

    public TrafficHunterAgentProperty property(final InputStream is) {
        return env.load(is);
    }

    public void retransform(final Instrumentation inst) {
        new Default()
                .ignore(ignoreMatchPackage())
                .type(getSpringComponentMatcher())
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(TransactionAdvise.class).on(isMethod()))
                ).installOn(inst);
    }

    public AgentMetadata setAgentMetadata(final Instant startTime, final AgentStatus status) {

        final String agentName = property().name();

        return new AgentMetadata(
                UUIDGenerator.generate(agentName),
                Environment.VERSION.version(),
                agentName,
                startTime,
                new AtomicReference<>(status)
        );
    }

    private Junction<TypeDescription> ignoreMatchPackage() {
        return ElementMatchers.nameStartsWith("java.")
                .or(ElementMatchers.nameStartsWith("sun."))
                .or(ElementMatchers.nameStartsWith("jdk."));
    }

    private ElementMatcher<TypeDescription> getSpringComponentMatcher() {
        return isAnnotatedWith(named(AnnotationPath.SERVICE.getPath()))
                .or(isAnnotatedWith(named(AnnotationPath.REST_CONTROLLER.getPath())))
                .or(isAnnotatedWith(named(AnnotationPath.CONTROLLER.getPath())))
                .or(isAnnotatedWith(named(AnnotationPath.REPOSITORY.getPath())));
    }

    /**
     * <p>
     * Intercepts method execution to create a span for tracing.
     * Handles span lifecycle, recording exceptions, and closing the scope.
     * </p>
     *
     * <p><b>Note:</b> Ensure the class has a <code>public</code> access modifier to avoid
     * visibility issues when used with external components or frameworks like ByteBuddy.
     * Using a private or package-private access modifier may result in runtime errors
     * due to restricted access.</p>
     *
     * @see TraceManager
     */
    public static class TransactionAdvise {

        public static final Tracer tracer = TraceManager.configure(new TraceExporter());

        @OnMethodEnter
        public static SpanScope enter(@Origin final String method) {

            Span currentSpan = Span.current();

            Span span = tracer.spanBuilder(method)
                    .setParent(Context.current().with(currentSpan))
                    .setAttribute("method.name", method)
                    .setStartTimestamp(Instant.now())
                    .startSpan();

            Scope scope = span.makeCurrent();

            return new SpanScope(span, scope);
        }

        @OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Enter final SpanScope spanScope, @Thrown final Throwable throwable) {

            if (Objects.nonNull(throwable)) {

                String exception = throwable.getClass().getName()
                        + " "
                        + "("
                        + throwable.getMessage()
                        + ")";

                spanScope.span().recordException(throwable);
                spanScope.span().setStatus(StatusCode.ERROR, exception);
            }

            spanScope.span().end(Instant.now());
            spanScope.scope().close();
        }
    }
}
