/*
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
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
package org.traffichunter.javaagent.plugin.logback;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.context.Context;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Local;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.traffichunter.javaagent.extension.AbstractPluginInstrumentation;
import org.traffichunter.javaagent.extension.Transformer;
import org.traffichunter.javaagent.plugin.sdk.CallDepth;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class LogbackPluginInstrumentation extends AbstractPluginInstrumentation {

    public LogbackPluginInstrumentation() {
        super("logback", LogbackPluginInstrumentation.class.getName(), "1.5.0");
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
        return named("ch.qos.logback.classic.Logger");
    }

    @Override
    public void transform(final Transformer transformer) {
        transformer.processAdvice(
                Advices.create(
                        ElementMatchers.isMethod()
                                .and(isPublic())
                                .and(named("callAppenders"))
                                .and(takesArguments(1))
                                .and(takesArgument(0, named("ch.qos.logback.classic.spi.ILoggingEvent"))),
                        CallAppendersAdvice.class
                )
        );
    }

    @SuppressWarnings("unused")
    public static class CallAppendersAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Argument(0) final ILoggingEvent event,
                                 @Local("thCallDepth") CallDepth callDepth) {

            callDepth = CallDepth.forClass(LoggerProvider.class);

            if(callDepth.getAndIncrement() > 0) {
                return;
            }

            LogbackPluginInstrumentationHelper.capture(Context.current(), event);
        }

        @OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Local("thCallDepth") CallDepth callDepth) {
            callDepth.decrementAndGet();
        }
    }
}
