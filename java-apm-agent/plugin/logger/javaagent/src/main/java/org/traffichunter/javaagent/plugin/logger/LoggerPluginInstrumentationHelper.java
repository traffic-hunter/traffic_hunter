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

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.context.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import traffichunter.java.util.logging.Logger;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public final class LoggerPluginInstrumentationHelper {

    public static void capture(final Logger logger, final Context context, final LogRecord record) {

        if(!logger.isLoggable(record.getLevel())) {
            return;
        }

        String message = record.getMessage();
        if(message == null) {
            return;
        }

        Level level = record.getLevel();
        if(level == null) {
            return;
        }

        AttributesBuilder attributes = Attributes.builder();

        Throwable thrown = record.getThrown();
        if(thrown != null) {
            attributes.put(AttributeKey.stringKey("exception.type"), thrown.getClass().getName());
            attributes.put(AttributeKey.stringKey("exception.message"), thrown.getMessage());
            StringWriter sw = new StringWriter();
            thrown.printStackTrace(new PrintWriter(sw));
            attributes.put(AttributeKey.stringKey("exception.stacktrace"), sw.toString());
        }

        Thread thread = Thread.currentThread();
        attributes.put(AttributeKey.stringKey("thread.name"), thread.getName());

        // java 19 below deprecated
        attributes.put(AttributeKey.longKey("thread.id"), thread.getId());

        GlobalOpenTelemetry.get()
                .getLogsBridge()
                .loggerBuilder(generateInstrumentationName(record.getLoggerName()))
                .build()
                .logRecordBuilder()
                .setSeverity(levelToSeverity(level))
                .setSeverityText(level.getName())
                .setTimestamp(record.getMillis(), TimeUnit.MILLISECONDS)
                .setBody(message)
                .setContext(context)
                .setAllAttributes(attributes.build())
                .emit();
    }

    private static String generateInstrumentationName(String logName) {
        return logName == null || logName.isEmpty() ? "ROOT" : logName;
    }

    private static Severity levelToSeverity(final Level level) {

        int lev = level.intValue();
        if (lev <= Level.FINEST.intValue()) {
            return Severity.TRACE;
        }
        if (lev <= Level.FINER.intValue()) {
            return Severity.DEBUG;
        }
        if (lev <= Level.FINE.intValue()) {
            return Severity.DEBUG2;
        }
        if (lev <= Level.CONFIG.intValue()) {
            return Severity.DEBUG3;
        }
        if (lev <= Level.INFO.intValue()) {
            return Severity.INFO;
        }
        if (lev <= Level.WARNING.intValue()) {
            return Severity.WARN;
        }
        if (lev <= Level.SEVERE.intValue()) {
            return Severity.ERROR;
        }
        return Severity.FATAL;
    }
}
