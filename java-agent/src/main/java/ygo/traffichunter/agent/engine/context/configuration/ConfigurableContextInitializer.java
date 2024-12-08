package ygo.traffichunter.agent.engine.context.configuration;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
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
import ygo.traffichunter.agent.engine.metric.transaction.TransactionInfo;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;
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
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
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
     * The {@code TransactionAdvise} inner class provides method-level advice for tracking transaction execution times
     * and errors. It uses ByteBuddy's {@link Advice} API to inject behavior into target methods.
     *
     * <p>Features:</p>
     * <ul>
     *     <li>Records the start and end times of method execution.</li>
     *     <li>Calculates the duration of the method call.</li>
     *     <li>Logs transaction information, including any thrown exceptions.</li>
     *     <li>Stores transaction data in the {@link SyncQueue} for later processing.</li>
     * </ul>
     */
    private static class TransactionAdvise {

        @OnMethodEnter
        public static Instant enter() {
            return Instant.now();
        }

        @OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Origin final String method,
                                @Enter final Instant startTime,
                                @Thrown final Throwable throwable) {

            final Instant endTime = Instant.now();
            final long duration = endTime.toEpochMilli() - startTime.toEpochMilli();

            final TransactionInfo txInfo = TransactionInfo.create(method,
                    startTime,
                    endTime,
                    duration,
                    throwable == null ? "No Error Message" : throwable.getMessage(),
                    throwable == null
            );

            SyncQueue.INSTANCE.add(txInfo);
        }
    }
}
