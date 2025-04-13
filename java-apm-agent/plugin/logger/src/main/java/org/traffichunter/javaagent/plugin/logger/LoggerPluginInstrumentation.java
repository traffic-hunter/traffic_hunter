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
package org.traffichunter.javaagent.plugin.logger;

import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.context.Context;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.bytebuddy.asm.Advice.Argument;
import net.bytebuddy.asm.Advice.Local;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;
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
public class LoggerPluginInstrumentation extends AbstractPluginInstrumentation {

    public LoggerPluginInstrumentation() {
        super("logger", LoggerPluginInstrumentation.class.getName(), "");
    }

    @Override
    public void transform(final Transformer transformer) {
        transformer.processAdvice(
                Advices.create(
                        ElementMatchers.isMethod()
                                .and(named("log"))
                                .and(takesArguments(1))
                                .and(takesArgument(0, LogRecord.class))
                                .and(isPublic()),
                        LoggerAdvice.class
                )
        );
    }

    @Override
    public ElementMatcher<? super TypeDescription> typeMatcher() {
        return named("java.util.Logging.Logger");
    }

    @SuppressWarnings("unused")
    public static class LoggerAdvice {

        @OnMethodEnter(suppress = Throwable.class)
        public static void enter(@This final Logger logger,
                                 @Argument(0) final LogRecord logRecord,
                                 @Local("thCallDepth") CallDepth callDepth) {

            callDepth = CallDepth.forClass(LoggerProvider.class);

            if(callDepth.getAndIncrement() == 0) {
                LoggerPluginInstrumentationHelper.capture(logger, Context.current(), logRecord);
            }
        }

        @OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Local("thCallDepth") CallDepth callDepth) {
            callDepth.decrementAndGet();
        }
    }
}
