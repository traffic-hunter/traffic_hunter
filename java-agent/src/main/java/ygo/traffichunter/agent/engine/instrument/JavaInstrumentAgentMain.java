package ygo.traffichunter.agent.engine.instrument;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.lang.instrument.Instrumentation;
import java.time.Instant;
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
import ygo.traffichunter.agent.engine.AgentExecutionEngine;
import ygo.traffichunter.agent.engine.env.Environment;
import ygo.traffichunter.agent.engine.instrument.annotation.AnnotationPath;
import ygo.traffichunter.agent.engine.instrument.bootstrap.BootState;
import ygo.traffichunter.agent.engine.queue.SyncQueue;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;

public class JavaInstrumentAgentMain {

    private static final Logger log = LoggerFactory.getLogger(JavaInstrumentAgentMain.class);

    private static final BootState STATE = new BootState();

    public static void premain(String agentArgs, Instrumentation inst) {

        final boolean success = STATE.start();
        if(!success) {
            log.error("traffic-hunter-agent-bootstrap already started. skipping agent loading.");
            return;
        }

        reTransform(inst);

        AgentExecutionEngine.run(Environment.SYSTEM_PROFILE.systemProfile());
    }

    private static void reTransform(final Instrumentation inst) {
        new Default()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .ignore(ignoreMatchPackage())
                .type(getSpringComponentMatcher())
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.visit(Advice.to(TransactionAdvise.class).on(isMethod()))
                ).installOn(inst);
    }

    private static Junction<TypeDescription> ignoreMatchPackage() {
        return ElementMatchers.nameStartsWith("java.")
                .or(ElementMatchers.nameStartsWith("sun."))
                .or(ElementMatchers.nameStartsWith("jdk."));
    }

    private static ElementMatcher<TypeDescription> getSpringComponentMatcher() {
        return isAnnotatedWith(named(AnnotationPath.SERVICE.getPath()))
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
