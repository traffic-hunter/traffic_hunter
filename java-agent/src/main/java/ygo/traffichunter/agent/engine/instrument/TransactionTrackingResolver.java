package ygo.traffichunter.agent.engine.instrument;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;

import java.lang.instrument.Instrumentation;
import java.time.Instant;
import net.bytebuddy.agent.builder.AgentBuilder.Default;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy;
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
import ygo.traffichunter.agent.engine.instrument.annotation.AnnotationPath;
import ygo.traffichunter.agent.engine.systeminfo.TransactionInfo;
import ygo.traffichunter.event.publisher.EventPublisher;
import ygo.traffichunter.event.publisher.TransactionEventPublisher;

public class TransactionTrackingResolver {

    private static final EventPublisher<TransactionInfo> eventPublisher = new TransactionEventPublisher();

    public static void agentmain(String agentArgs, Instrumentation inst) {
        new Default()
                .with(RedefinitionStrategy.RETRANSFORMATION)
                .with(NoOp.INSTANCE)
                .with(TypeStrategy.Default.REDEFINE)
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
                    throwable != null
            );

            eventPublisher.publish(txInfo);
        }
    }
}
