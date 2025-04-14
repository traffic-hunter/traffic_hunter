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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.context.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class LogbackPluginInstrumentationHelper {

    private static final AttributeKey<String> THREAD_NAME = AttributeKey.stringKey("thread.name");
    private static final AttributeKey<Long> THREAD_ID = AttributeKey.longKey("thread.id");

    private static final AttributeKey<String> EXCEPTION_TYPE = AttributeKey.stringKey("exception.type");
    private static final AttributeKey<String> EXCEPTION_MESSAGE = AttributeKey.stringKey("exception.message");
    private static final AttributeKey<String> EXCEPTION_STACKTRACE = AttributeKey.stringKey("exception.stacktrace");

    public static void capture(final Context context, final ILoggingEvent event) {

        String body = event.getFormattedMessage();
        if(body == null) {
            return;
        }

        Level level = event.getLevel();
        if(level == null) {
            return;
        }

        AttributesBuilder attributes = Attributes.builder();

        Object throwableProxy = event.getThrowableProxy();
        Throwable throwable = null;
        if(throwableProxy instanceof ThrowableProxy) {
           throwable = ((ThrowableProxy) throwableProxy).getThrowable();
        }
        if(throwable != null) {
            attributes.put(EXCEPTION_TYPE, throwable.getClass().getName());
            attributes.put(EXCEPTION_MESSAGE, throwable.getMessage());
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            attributes.put(EXCEPTION_STACKTRACE, sw.toString());
        }

        attributes.put(THREAD_NAME, event.getThreadName());
        attributes.put(THREAD_ID, Thread.currentThread().getId());

        GlobalOpenTelemetry.get()
                .getLogsBridge()
                .loggerBuilder(generateInstrumentationName(event.getLoggerName()))
                .build()
                .logRecordBuilder()
                .setContext(context)
                .setSeverity(levelToSeverity(level))
                .setSeverityText(level.levelStr)
                .setTimestamp(event.getTimeStamp(), TimeUnit.MILLISECONDS)
                .setBody(body)
                .setAllAttributes(attributes.build())
                .emit();
    }

    private static String generateInstrumentationName(String logName) {
        return logName == null || logName.isEmpty() ? "ROOT" : logName;
    }

    private static Severity levelToSeverity(final Level level) {

        return switch (level.levelInt) {
            case Level.ALL_INT, Level.TRACE_INT -> Severity.TRACE;
            case Level.DEBUG_INT -> Severity.DEBUG;
            case Level.INFO_INT -> Severity.INFO;
            case Level.WARN_INT -> Severity.WARN;
            case Level.ERROR_INT -> Severity.ERROR;
            default -> Severity.UNDEFINED_SEVERITY_NUMBER;
        };
    }
}
