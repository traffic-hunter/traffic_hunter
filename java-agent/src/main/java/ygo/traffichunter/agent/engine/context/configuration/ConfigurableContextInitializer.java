package ygo.traffichunter.agent.engine.context.configuration;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.time.Instant;
import java.util.UUID;
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
import ygo.traffichunter.agent.engine.jvm.JVMSelector;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

public class ConfigurableContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableContextInitializer.class);

    private final ConfigurableEnvironment env;

    private final UUID agentId = UUID.randomUUID();

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
        return new AgentMetadata(
                agentId.toString(),
                Environment.VERSION.version(),
                property().name(),
                startTime,
                status
        );
    }

    @Deprecated(since = "1.0")
    public void attach(final TrafficHunterAgentProperty property) {
        try {

            VirtualMachine vm = JVMSelector.getVM(property.targetJVMPath());

            vm.loadAgent(property.jar());
        } catch (IOException | AgentLoadException | AgentInitializationException e) {
            log.error("Failed to load agent = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Junction<TypeDescription> ignoreMatchPackage() {
        return ElementMatchers.nameStartsWith("java.")
                .or(ElementMatchers.nameStartsWith("sun."))
                .or(ElementMatchers.nameStartsWith("jdk."));
    }

    private ElementMatcher<TypeDescription> getSpringComponentMatcher() {
        return isAnnotatedWith(named(AnnotationPath.SERVICE.getPath()))
                .or(named(AnnotationPath.REST_CONTROLLER.getPath()))
                .or(isAnnotatedWith(named(AnnotationPath.REPOSITORY.getPath())));
    }

    private static class TransactionAdvise {

        @OnMethodEnter
        public static long enter() {
            return Instant.now().toEpochMilli();
        }

        @OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Origin final String method,
                                @Enter final long startTime,
                                @Thrown final Throwable throwable) {

            final long endTime = Instant.now().toEpochMilli();
            final long duration = endTime - startTime;

            final TransactionInfo txInfo = TransactionInfo.create(method,
                    Instant.ofEpochMilli(startTime),
                    Instant.ofEpochMilli(endTime),
                    duration,
                    throwable == null ? "No Error Message" : throwable.getMessage(),
                    throwable == null
            );

            SyncQueue.INSTANCE.add(txInfo);
        }
    }
}
